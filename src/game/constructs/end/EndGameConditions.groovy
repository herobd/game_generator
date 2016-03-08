package game.constructs.end

import game.constructs.board.Board
import game.constructs.condition.Conditional
import game.constructs.condition.TerminalConditional
import game.constructs.condition.functions.Function
import game.constructs.condition.result.EndGameResult
import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.goal.GoalClause
import game.gdl.clauses.goal.HasGoalClause
import game.gdl.clauses.terminal.HasTerminalClause
import game.gdl.clauses.terminal.TerminalClause
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.statement.Tokens

/**
 * @author Lawrence Thatcher
 *
 * A class used to store and represent the ending conditions for a game.
 */
class EndGameConditions implements HasClauses, HasGoalClause, HasTerminalClause
{
	private List<TerminalConditional> conditions
	private Board board
	EndGameConditions(List<TerminalConditional> conditions, Board board)
	{
		this.conditions = conditions
		this.board = board
	}

	/**
	 * Retrieves a list of all the Functions used within the conditionals,
	 * regardless of if they are supported by the board or not.
	 * @return a list of Functions
	 */
	List<Function> getUsedFunctions()
	{
		def F = []
		for (Conditional c : conditions)
		{
			F.add(c.antecedent)
		}
		return F
	}

	/**
	 * Retrieves all of the conditionals
	 * @return
	 */
	List<Conditional> getConditionals()
	{
		return this.conditions
	}

	/**
	 * Retrieves a list of all the conditionals that can be supported by the current board
	 * @return
	 */
	Collection<Conditional> getSupportedConditionals()
	{
		def S = new HashSet()
		for (Conditional c : conditions)
		{
			if (supportsAllFunctions(c))
				S += c
		}
		return S
	}
	//TODO: add some sort of reducer to get a partial conditional..?

	/**
	 * Retrieves all of the GDL clauses from the board that correspond to supported functions
	 * @return
	 */
	Collection<GDLClause> getSupportedBoardGDLClauses()
	{
		def clauses = []
		for (Conditional c : supportedConditionals)
		{
			for (Function f : c.functions)
			{
				def clause = board.getImplementation(f)
				clauses.add(clause)
			}
		}
		return clauses
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return [terminalClause, goalClause]
	}

	@Override
	GoalClause getGoalClause()
	{
		return generateGoalStatements()
	}

	@Override
	TerminalClause getTerminalClause()
	{
		return generateTerminalStatements()
	}

	// HELPER FUNCTIONS

	private boolean supportsAllFunctions(Conditional c)
	{
		for (Function f : c.functions)
		{
			if (!board.supports(f))
				return false
		}
		return true
	}

	private TerminalClause generateTerminalStatements()
	{
		def T = []
		for (Conditional c : supportedConditionals)
		{
			def sig = c.antecedent.GDL_Signature
			def s
			if (sig instanceof GString)
			{
				s = GString.EMPTY
				s += "(<= terminal\n("
				s += c.antecedent.GDL_Signature
				s += "))"
			}
			else
			{
				s = "(<= terminal\n("
				s += c.antecedent.GDL_Signature
				s += "))"
			}
			if (s instanceof GString)
				T.add(new GeneratorStatement(s))
			else
				T.add(new SimpleStatement(s))
		}
		return new TerminalClause(T)
	}

	private GoalClause generateGoalStatements()
	{
		def T = []
		for (Conditional c : supportedConditionals)
		{
			switch (c.consequent)
			{
				case EndGameResult.Win:
					GString s = "(<= (goal ${Tokens.PLAYER} 100)\n"
					s += "("
					s += c.antecedent.GDL_Signature
					s += "))"
					T.add(new GeneratorStatement(s))
					break
			}
		}
//		String s =
//			"(<= (goal White 100)\n" +
//			"(3inARow x))\n" +
//			"\n" +
//			"(<= (goal White 50)\n" +
//			"(not (3inARow x))\n" +
//			"(not (3inARow o))\n" +
//			"(not open))\n" +
//			"\n" +
//			"(<= (goal White 0)\n" +
//			"(3inARow o))\n" +
//			"\n" +
//			"(<= (goal Black 100)\n" +
//			"(3inARow o))\n" +
//			"\n" +
//			"(<= (goal Black 50)\n" +
//			"(not (3inARow x))\n" +
//			"(not (3inARow o))\n" +
//			"(not open))\n" +
//			"\n" +
//			"(<= (goal Black 0)\n" +
//			"(line x))"
		return new GoalClause(T)
	}
}

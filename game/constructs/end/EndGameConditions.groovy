package constructs.end

import constructs.board.Board
import constructs.condition.Conditional
import constructs.condition.TerminalConditional
import constructs.condition.functions.Function
import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.goal.GoalClause
import gdl.clauses.goal.HasGoalClause
import gdl.clauses.terminal.HasTerminalClause
import gdl.clauses.terminal.TerminalClause

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

	private static TerminalClause generateTerminalStatements()
	{
		def T = []
		//TODO: BAD! don't hard-code, use generators
//		for (Conditional c : supportedConditionals)
//		{
//			String s = "(<= terminal\n("
//			s += c.antecedent.toString()
//			s += "))"
//			T.add(new GDLStatement(s))
//		}

		String s =
				"(<= terminal\n" +
				"(3inARow x))\n" +
				"\n" +
				"(<= terminal\n" +
				"(3inARow o))\n" +
				"\n" +
				"(<= terminal\n" +
				"(not open))"
		T.add(new GDLStatement(s))

		return new TerminalClause(T)
	}

	private static GoalClause generateGoalStatements()
	{
		String s =
			"(<= (goal White 100)\n" +
			"(line x))\n" +
			"\n" +
			"(<= (goal White 50)\n" +
			"(not (line x))\n" +
			"(not (line o))\n" +
			"(not open))\n" +
			"\n" +
			"(<= (goal White 0)\n" +
			"(line o))\n" +
			"\n" +
			"(<= (goal Black 100)\n" +
			"(line o))\n" +
			"\n" +
			"(<= (goal Black 50)\n" +
			"(not (line x))\n" +
			"(not (line o))\n" +
			"(not open))\n" +
			"\n" +
			"(<= (goal Black 0)\n" +
			"(line x))"
		return new GoalClause([new GDLStatement(s)])
	}
}

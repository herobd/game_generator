package constructs.end

import constructs.board.Board
import constructs.condition.Conditional
import constructs.condition.TerminalCondition
import constructs.condition.functions.Function
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 *
 * A class used to store and represent the ending conditions for a game.
 */
class EndGameConditions implements HasClauses
{
	private List<TerminalCondition> conditions
	private Board board
	EndGameConditions(List<TerminalCondition> conditions, Board board)
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
	List<Conditional> getConditions()
	{
		return this.conditions
	}

	/**
	 * Retrieves a list of all the conditionals that can be supported by the current board
	 * @return
	 */
	List<Conditional> getSupportedConditionals()
	{
		def F = []
		for (Conditional c : conditions)
		{
			if (board.supports(c.antecedent))
				F.add(c)
		}
		return F
	}

	/**
	 * Retrieves all of the GDL clauses from the board that correspond to t
	 * @return
	 */
	Collection<GDLClause> getSupportedBoardGDLClauses()
	{
		def clauses = []
		for (Conditional c : supportedConditionals)
		{
			def clause = board.getImplementation(c.antecedent)
			clauses.add(clause)
		}
		return clauses
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

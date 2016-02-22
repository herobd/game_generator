package constructs.board

import constructs.condition.functions.Function
import constructs.condition.functions.GameFunction
import constructs.condition.functions.Supports
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 *
 * Stores information about the game board.
 */
class Board implements HasClauses, Supports
{
	/**
	 * Retrieves the GDL-description and implementation of a particular function
	 * relative to the current game board. If that function is not supported by
	 * the current setup, this method will throw a FunctionNotSupportedException.
	 *
	 * @param func The function to retrieve the implementation for.
	 * @return A GDL clause giving the description of that function.
	 */
	GDLClause getImplementation(GameFunction func)
	{
		try
		{
			def clause = func.retrieveBoardGDL(this)
			return clause
		}
		catch (MissingMethodException ex)
		{
			throw new FunctionNotSupportedException(func, ex)
		}
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}

	@Override
	List<Function> getSupportedFunctions()
	{
		return []
	}
}

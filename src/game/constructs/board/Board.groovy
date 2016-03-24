package game.constructs.board

import game.constructs.condition.functions.Function
import game.constructs.condition.functions.Supports
import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses

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
	GDLClause getImplementation(Function func)
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
	boolean supports(Function function)
	{
		try
		{
			function.retrieveBoardGDL(this)
			return true
		}
		catch (MissingMethodException ignore)
		{
			return false
		}
	}
	
	int getNumParams()
	{
	    //TODO
	    return 0;
	}
}

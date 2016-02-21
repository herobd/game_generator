package constructs.board

import constructs.condition.functions.Function
import constructs.condition.functions.GameFunction
import constructs.condition.functions.Supports
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 *
 * stores information about the game board.
 */
class Board implements HasClauses, Supports
{
	List<GDLClause> getImplementations(List<GameFunction> funcs)
	{
		def result = []
		for (GameFunction f : funcs)
		{
			try
			{
				def clause = f.retrieveBoardGDL(this)
				result.add(clause)
			}
			catch (MissingMethodException ex)
			{
				throw new FunctionNotSupportedException(f, ex)
			}
		}
		return result
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

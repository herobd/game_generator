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
	GDLClause getImplementation(GameFunction p)
	{
		return null
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

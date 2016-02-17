package constructs.board

import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 *
 * stores information about the game board.
 */
class Board implements HasClauses
{
	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

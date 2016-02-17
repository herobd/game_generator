package constructs.board

import gdl.GDLClause
import gdl.HasClauses

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

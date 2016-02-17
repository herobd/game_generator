package constructs.pieces

import gdl.GDLClause
import gdl.HasClauses

/**
 * @author Lawrence Thatcher
 */
class Piece implements HasClauses
{
	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

package constructs.pieces

import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

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

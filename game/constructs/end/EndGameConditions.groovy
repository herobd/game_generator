package constructs.end

import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 * A class used to store and represent the ending conditions for a game
 */
class EndGameConditions implements HasClauses
{
	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

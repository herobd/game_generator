package constructs.end

import gdl.GDLClause
import gdl.HasClauses

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

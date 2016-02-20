package constructs.end

import constructs.condition.Condition
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 * A class used to store and represent the ending conditions for a game
 */
class EndGameConditions implements HasClauses
{
	List<Condition> conditions
	EndGameConditions(List<Condition> conditions)
	{
		this.conditions = conditions
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

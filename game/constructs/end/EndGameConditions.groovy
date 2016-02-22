package constructs.end

import constructs.condition.Condition
import constructs.condition.functions.Function
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

	List<Function> getUsedFunctions()
	{
		def F = []
		for (Condition c : conditions)
		{
			F.add(c.antecedent)
		}
		return F
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

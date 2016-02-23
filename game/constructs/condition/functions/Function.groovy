package constructs.condition.functions

import constructs.condition.Condition
import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
trait Function implements Condition
{
	abstract GameFunction getType()

	abstract String getFunctionName()

	abstract GDLClause retrieveBoardGDL(Object)

	Collection<Function> getFunctions()
	{
		return [this]
	}
}
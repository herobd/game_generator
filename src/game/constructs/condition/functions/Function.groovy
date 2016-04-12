package game.constructs.condition.functions

import game.constructs.condition.PreCondition
import game.gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */

trait Function implements PreCondition {

	abstract GameFunction getType()

	abstract String getFunctionName()

	abstract GDLClause retrieveBoardGDL(Object)

	Collection<Function> getFunctions()
	{
		return [this]
	}
}

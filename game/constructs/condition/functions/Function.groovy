package constructs.condition.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
interface Function
{
	GameFunction getType()

	String getFunctionName()

	GDLClause retrieveBoardGDL(Object)
}
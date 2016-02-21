package constructs.condition.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
interface GameFunction
{
	Function getType()

	String getFunctionName()

	GDLClause retrieveBoardGDL(Object)
}
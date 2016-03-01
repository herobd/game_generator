package game.constructs.condition.functions

import game.gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
interface SupportsOpen extends Supports
{
	GDLClause open()
}
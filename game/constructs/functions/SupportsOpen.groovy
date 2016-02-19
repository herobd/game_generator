package constructs.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
interface SupportsOpen extends Supports
{
	GDLClause open()
}
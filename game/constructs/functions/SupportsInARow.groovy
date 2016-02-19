package constructs.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
interface SupportsInARow extends Supports
{
	GDLClause in_a_row(int n)
}
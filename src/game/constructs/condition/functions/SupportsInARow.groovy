package game.constructs.condition.functions

import game.gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
interface SupportsInARow extends Supports
{
	GDLClause in_a_row(int n)
}
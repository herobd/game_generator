package gdl.clauses.dynamic

import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType
import gdl.GDLStatement

/**
 * @author Lawrence Thatcher
 *
 * Clause for statements in the Dynamic Components category
 */
class DynamicComponentsClause extends AbstractClause
{
	DynamicComponentsClause(List<GDLStatement> statements)
	{
		super(ClauseType.DynamicComponents, statements)
	}
}

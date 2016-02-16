package gdl.dynamic

import gdl.AbstractClause
import gdl.ClauseType
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

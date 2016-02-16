package gdl.base

import gdl.AbstractClause
import gdl.ClauseType
import gdl.GDLStatement

/**
 * @author Lawrence Thatcher
 *
 * Clause for statements in the Base + Input category
 */
class BaseClause extends AbstractClause
{
	BaseClause(List<GDLStatement> statements)
	{
		super(ClauseType.BaseAndInput, statements)
	}
}

package gdl.clauses.base

import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType
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

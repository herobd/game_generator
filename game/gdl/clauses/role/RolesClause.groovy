package gdl.clauses.role

import gdl.GDLStatement
import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Roles category
 */
class RolesClause extends AbstractClause
{
	RolesClause(List<GDLStatement> statements)
	{
		super(ClauseType.Roles, statements)
	}
}

package game.gdl.clauses.role

import game.gdl.statement.SimpleStatement
import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Roles category
 */
class RolesClause extends AbstractClause
{
	RolesClause(List<SimpleStatement> statements)
	{
		super(ClauseType.Roles, statements)
	}
}

package game.gdl.clauses.base

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.statement.GDLStatement

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

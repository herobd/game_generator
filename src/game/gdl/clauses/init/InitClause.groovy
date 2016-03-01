package game.gdl.clauses.init

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.GDLStatement

/**
 * @author Lawrence Thatcher
 *
 * Clause for statements in the Initial State category
 */
class InitClause extends AbstractClause
{
	InitClause(List<GDLStatement> statements)
	{
		super(ClauseType.InitialState, statements)
	}
}

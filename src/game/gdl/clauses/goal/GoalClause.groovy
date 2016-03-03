package game.gdl.clauses.goal

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.statement.GDLStatement

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Goal category
 */
class GoalClause extends AbstractClause
{
	GoalClause(List<GDLStatement> statements)
	{
		super(ClauseType.Goal, statements)
	}
}

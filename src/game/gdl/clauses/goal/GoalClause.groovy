package game.gdl.clauses.goal

import game.gdl.statement.GDLStatement
import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType

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

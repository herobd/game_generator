package game.gdl.clauses.goal

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Goal category
 */
class GoalClause extends AbstractClause
{
	GoalClause(List<SimpleStatement> statements)
	{
		super(ClauseType.Goal, statements)
	}
}

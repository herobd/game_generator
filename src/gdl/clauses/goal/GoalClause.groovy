package gdl.clauses.goal

import gdl.GDLStatement
import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType

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

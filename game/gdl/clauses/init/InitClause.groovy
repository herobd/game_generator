package gdl.clauses.init

import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType
import gdl.GDLStatement

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

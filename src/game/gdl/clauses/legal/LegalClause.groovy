package game.gdl.clauses.legal

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.statement.GDLStatement

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Legal category
 */
class LegalClause extends AbstractClause
{
	LegalClause(List<GDLStatement> statements)
	{
		super(ClauseType.Legal, statements)
	}
}

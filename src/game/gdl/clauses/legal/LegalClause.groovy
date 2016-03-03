package game.gdl.clauses.legal

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Legal category
 */
class LegalClause extends AbstractClause
{
	LegalClause(List<SimpleStatement> statements)
	{
		super(ClauseType.Legal, statements)
	}
}

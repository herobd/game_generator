package gdl.clauses.legal

import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType
import gdl.GDLStatement

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

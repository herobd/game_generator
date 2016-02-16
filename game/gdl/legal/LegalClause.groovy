package gdl.legal

import gdl.AbstractClause
import gdl.ClauseType
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

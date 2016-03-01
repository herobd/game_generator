package gdl.clauses.terminal

import gdl.GDLStatement
import gdl.clauses.AbstractClause
import gdl.clauses.ClauseType

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Terminal category
 */
class TerminalClause extends AbstractClause
{
	TerminalClause(List<GDLStatement> statements)
	{
		super(ClauseType.Terminal, statements)
	}
}

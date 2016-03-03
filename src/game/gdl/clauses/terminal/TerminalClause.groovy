package game.gdl.clauses.terminal

import game.gdl.statement.GDLStatement
import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType

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

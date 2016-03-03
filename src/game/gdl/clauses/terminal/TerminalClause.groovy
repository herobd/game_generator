package game.gdl.clauses.terminal

import game.gdl.statement.SimpleStatement
import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType

/**
 * @author Lawrence Thatcher
 *
 *Clause for statements in the Terminal category
 */
class TerminalClause extends AbstractClause
{
	TerminalClause(List<SimpleStatement> statements)
	{
		super(ClauseType.Terminal, statements)
	}
}

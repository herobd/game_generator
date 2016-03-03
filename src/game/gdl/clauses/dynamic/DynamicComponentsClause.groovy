package game.gdl.clauses.dynamic

import game.gdl.clauses.AbstractClause
import game.gdl.clauses.ClauseType
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 *
 * Clause for statements in the Dynamic Components category
 */
class DynamicComponentsClause extends AbstractClause
{
	DynamicComponentsClause(List<SimpleStatement> statements)
	{
		super(ClauseType.DynamicComponents, statements)
	}
}

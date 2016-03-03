package game.constructs.pieces

import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.dynamic.HasDynCompClause
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 *
 * The types of piece placement schemes supported.
 *
 * Persistent - once a piece has been placed, it stays there (ex: Tic-Tac-Toe)
 */
enum Placement implements HasClauses, HasDynCompClause
{
	Persistent([persistenceGDL])

	private static final SimpleStatement getPersistenceGDL()
	{
		return new SimpleStatement(
				"(<= (next (cell ?m ?n ?w))\n" +
				"(true (cell ?m ?n ?w))\n" +
				"(distinct ?w b))")
	}

	private DynamicComponentsClause dynComp
	private Placement(List<SimpleStatement> statements)
	{
		this.dynComp = new DynamicComponentsClause(statements)
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return [this.dynComp]
	}

	@Override
	DynamicComponentsClause getDynamicComponentClause()
	{
		return this.dynComp
	}
}

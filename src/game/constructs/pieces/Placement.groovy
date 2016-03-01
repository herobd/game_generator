package game.constructs.pieces

import game.gdl.statement.GDLStatement
import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.dynamic.HasDynCompClause

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

	private static final GDLStatement getPersistenceGDL()
	{
		return new GDLStatement(
				"(<= (next (cell ?m ?n ?w))\n" +
				"(true (cell ?m ?n ?w))\n" +
				"(distinct ?w b))")
	}

	private DynamicComponentsClause dynComp
	private Placement(List<GDLStatement> statements)
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

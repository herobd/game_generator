package constructs.pieces

import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.dynamic.DynamicComponentsClause
import gdl.clauses.dynamic.HasDynCompClause

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

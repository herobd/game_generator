package constructs

import gdl.GDLClause
import gdl.GDLStatement
import gdl.HasClauses
import gdl.dynamic.DynamicComponentsClause
import gdl.dynamic.HasDynCompClause
import gdl.legal.HasLegalClause
import gdl.legal.LegalClause

/**
 * @author Lawrence Thatcher
 *
 * An enum representing the different supported types of turn orders.
 *
 * Alternating - Players alternate/cycle taking turns
 * (Future types might also include concurrent, speed, etc..)
 */
enum TurnOrder implements HasClauses, HasLegalClause, HasDynCompClause
{
	Alternating([white_noop, black_noop], [white_next, black_next])


	//TODO: Replace these with generic form
	private static final GDLStatement white_noop = new GDLStatement(
			"(<= (legal White noop)\n" +
			"(true (control Black)))")

	private static final GDLStatement black_noop = new GDLStatement(
			"(<= (legal Black noop)\n" +
			"(true (control White)))")

	private static final GDLStatement white_next = new GDLStatement(
			"(<= (next (control White))\n" +
			"(true (control Black)))")

	private static final GDLStatement black_next = new GDLStatement(
			"(<= (next (control Black))\n" +
			"(true (control White)))")

	private LegalClause legal
	private DynamicComponentsClause dynComp
	private TurnOrder(List<GDLStatement> ls, List<GDLStatement> dcs)
	{
		this.legal = new LegalClause(ls)
		this.dynComp = new DynamicComponentsClause(dcs)
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return [legal, dynComp]
	}

	@Override
	DynamicComponentsClause getDynamicComponentClause()
	{
		return this.dynComp
	}

	@Override
	LegalClause getLegalClause()
	{
		return this.legal
	}
}
package constructs

import gdl.GDLClause
import gdl.GDLStatement
import gdl.HasClauses
import gdl.base.BaseClause
import gdl.base.HasBaseClause
import gdl.dynamic.DynamicComponentsClause
import gdl.dynamic.HasDynCompClause
import gdl.init.HasInitClause
import gdl.init.InitClause
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
enum TurnOrder implements HasClauses, HasLegalClause, HasDynCompClause, HasBaseClause, HasInitClause
{
	Alternating([white_noop, black_noop], [white_next, black_next], [control_base, input_noop], [control_init])


	//TODO: Replace these with generic form
	private static final GDLStatement getWhite_noop()
	{
		return new GDLStatement(
				"(<= (legal White noop)\n" +
				"(true (control Black)))")
	}

	private static final GDLStatement getBlack_noop()
	{
		return new GDLStatement(
				"(<= (legal Black noop)\n" +
				"(true (control White)))")
	}

	private static final GDLStatement getWhite_next()
	{
		return new GDLStatement(
				"(<= (next (control White))\n" +
				"(true (control Black)))")
	}

	private static final GDLStatement getBlack_next()
	{
		return new GDLStatement(
				"(<= (next (control Black))\n" +
				"(true (control White)))")
	}

	private static final GDLStatement getInput_noop()
	{
		return new GDLStatement("(<= (input ?p noop) (role ?p))")
	}

	private static final GDLStatement getControl_base()
	{
		return new GDLStatement("(<= (base (control ?p)) (role ?p))")
	}

	private static final GDLStatement getControl_init()
	{
		return new GDLStatement("(init (control White))")
	}

	private LegalClause legal
	private DynamicComponentsClause dynComp
	private BaseClause base
	private InitClause initState
	private TurnOrder(List<GDLStatement> ls, List<GDLStatement> dcs, List<GDLStatement> bis, List<GDLStatement> is)
	{
		this.legal = new LegalClause(ls)
		this.dynComp = new DynamicComponentsClause(dcs)
		this.base = new BaseClause(bis)
		this.initState = new InitClause(is)
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return [legal, dynComp, base, initState]
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

	@Override
	BaseClause getBaseAndInputClause()
	{
		return this.base
	}

	@Override
	InitClause getInitialStateClause()
	{
		return this.initState
	}
}
package game.constructs

import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.base.BaseClause
import game.gdl.clauses.base.HasBaseClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.dynamic.HasDynCompClause
import game.gdl.clauses.init.HasInitClause
import game.gdl.clauses.init.InitClause
import game.gdl.clauses.legal.HasLegalClause
import game.gdl.clauses.legal.LegalClause
import game.gdl.statement.GDLStatement
import game.gdl.statement.SimpleStatement

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
	private static final SimpleStatement getWhite_noop()
	{
		return new SimpleStatement(
				"(<= (legal White noop)\n" +
				"(true (control Black)))")
	}

	private static final SimpleStatement getBlack_noop()
	{
		return new SimpleStatement(
				"(<= (legal Black noop)\n" +
				"(true (control White)))")
	}

	private static final SimpleStatement getWhite_next()
	{
		return new SimpleStatement(
				"(<= (next (control White))\n" +
				"(true (control Black)))")
	}

	private static final SimpleStatement getBlack_next()
	{
		return new SimpleStatement(
				"(<= (next (control Black))\n" +
				"(true (control White)))")
	}

	private static final SimpleStatement getInput_noop()
	{
		return new SimpleStatement("(<= (input ?p noop) (role ?p))")
	}

	private static final SimpleStatement getControl_base()
	{
		return new SimpleStatement("(<= (base (control ?p)) (role ?p))")
	}

	private static final SimpleStatement getControl_init()
	{
		return new SimpleStatement("(init (control White))")
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
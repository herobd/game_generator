package constructs.pieces

import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.base.BaseClause
import gdl.clauses.base.HasBaseClause
import gdl.clauses.dynamic.DynamicComponentsClause
import gdl.clauses.dynamic.HasDynCompClause
import gdl.clauses.legal.HasLegalClause
import gdl.clauses.legal.LegalClause

/**
 * @author Lawrence Thatcher
 *
 * Contains the GDL descriptions of a legal move that can be performed by a piece
 */
class Move implements HasClauses, HasDynCompClause, HasBaseClause, HasLegalClause
{
	private def inputs = []
	private DynamicComponentsClause dynComp
	private BaseClause base
	private LegalClause legal

	/**
	 * Defines the Move
	 * TODO: remove last two arguments
	 * @param inputs 			The inputs the Input and Legal clauses will be generated from
	 * @param dynCompStatements The predefined GDL definitions of the way this move works
	 * @param i					(temporary) the input clause statement
	 * @param l					(temporary) the legal clause statement
	 */
	Move(List<String> inputs, List<GDLStatement> dynCompStatements, GDLStatement i, GDLStatement l)
	{
		//TODO: generate Input and Legal statements using inputs, rather than hard-coding
		this.inputs = inputs
		this.dynComp = new DynamicComponentsClause(dynCompStatements)
		this.base = new BaseClause([i])
		this.legal = new LegalClause([l])
	}

	@Override
	BaseClause getBaseAndInputClause()
	{
		return this.base
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return [base, dynComp, legal]
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

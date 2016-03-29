package game.constructs.pieces

import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.base.BaseClause
import game.gdl.clauses.base.HasBaseClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.dynamic.HasDynCompClause
import game.gdl.clauses.legal.HasLegalClause
import game.gdl.clauses.legal.LegalClause
import game.gdl.statement.GDLStatement
import generator.FineTunable

/**
 * @author Lawrence Thatcher
 *
 * Contains the GDL descriptions of a legal move that can be performed by a piece
 */
class Move implements HasClauses, HasDynCompClause, HasBaseClause, HasLegalClause, FineTunable
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

    int complexityCount()
    {
        //TODO fill this is
        return 1
    }
    
    @Override
    int getNumParams()
	{
	    //TODO fill this is
        return 0
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        //TODO
        
    }
}

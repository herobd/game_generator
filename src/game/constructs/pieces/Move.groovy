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
class Move implements HasClauses, FineTunable //HasDynCompClause, HasBaseClause, HasLegalClause,
{
	/*private def inputs = []
	private DynamicComponentsClause dynComp
	private BaseClause base
	private LegalClause legal*/
	
	private SimpleStatement input
	
	private Condition precondition=null
	private List<Action> postconditions=[]
	
	private String id=''

	/**
	 * Defines the Move
	 * TODO: remove last two arguments
	 * @param inputs 			The inputs the Input and Legal clauses will be generated from
	 * @param dynCompStatements The predefined GDL definitions of the way this move works
	 * @param i					(temporary) the input clause statement
	 * @param l					(temporary) the legal clause statement
	 *
	Move(List<String> inputs, List<GDLStatement> dynCompStatements, GDLStatement i, GDLStatement l)
	{
		//TODO: generate Input and Legal statements using inputs, rather than hard-coding
		this.inputs = inputs
		this.dynComp = new DynamicComponentsClause(dynCompStatements)
		this.base = new BaseClause([i])
		this.legal = new LegalClause([l])
	}*/
	
	Move()
	{
	
	}
	
	Move(Condition precondition, List<Action> postconditions)
	{
	    this.precondition = precondition
	    this.postconditions = postconditions
	}

    String getId() 
    {
        return id
    }
    void setId(String id)
    {
        this.id=id
        input = new SimpleStatement("(<= (input ?p ("+id+" ?x ?y)) (index ?x) (index ?y) (role ?p))")
    }

	

	@Override
	Collection<GDLClause> getGDLClauses(Board board)
	{
		//return [base, dynComp, legal]
		def toRet=[precondition.getGDLClauses(id),compilePostconditions(board)]
		//for (Action a : postconditions)
		    //toRet.push(a.getGDLClauses(id));
	    return toRet
	}
	
	GDLClause compilePostconditions(Board board) //List<Actions> postconditions, String piece_id, String move_id
	{
	    List< List<string> > effectedSpaces=[];
	    String moveParams=''
	    string clause = "(<= (next "
	    for (Action a : postconditions)
	    {
	        clause += a.effect(board) //The effects of the postcondition, like "(cell ?mTo ?nTo ${GameToken.PLAYER_NAME}"+"_"+piece_id+")"
	        effectedSpaces.push(a.effected(board)) //the vaiables the effects use, like "?mTo", or possiblely adjcent type identifiers
	        moveParams+=a.params(board)+" " //the variables that the player selects
	    }
	    clause += board.getGeneralSpaceGDL() + ')' // general space is like "(cell ?m ?n ?var)"
	    //end next
	    
	    clause += "(does ${GameToken.PLAYER_NAME} ("+move_id+" "+moveParams+"))"
	    
	    //define all the uneffected spaces to be the same
	    clause += '(true '+board.getGeneralSpace() + ')'
	    for (List<string> space : effectedSpaces)
	    {
	        if (space.length==0) {
	            clause += '(distinct '+board.getGeneralSpaceGDLIndex(0)+' '+space[0]+')'
	        }
	        else
	        {
	            clause += '(or '
	            for (int i=0; i<space.length; i++) //does 'or' support more than two parameters?
	            {
	                clause += '(distinct '+board.getGeneralSpaceGDLIndex(i)+' '+space[i]+')'
	            }
	            clause += ')'
	        }
	    }
	    clause+=")"
	    return new GeneratorStatement(cluase)
	}
	
	/*reference code
	{
	    //String name = Integer.toString(n[0]) + "inARow"
	    
		//define all next for this postcondition
		def x = new GeneratorStatement("(<= (next (cell ?mTo ?nTo ${GameToken.PLAYER_NAME}"+"_"+piece_id+) \
		                                          (cell ?mFrom ?nFrom b) \
		                                          (cell ?m ?n ?var) ) \
                                            (does player_name ("+move_id+" ?mTo ?nTo ?mFrom ?nFrom)) \
                                            (true (cell ?m ?n ?var)) \
                                            (or (distinct ?m ?mTo) \
                                                (distinct ?n ?nTo) ) \
                                            (or (distinct ?m ?mFrom) \
                                                (distinct ?n ?nFrom) ) \
                                        )")
        return x
	}*/

   /* @Override
	BaseClause getBaseAndInputClause()
	{
		return this.base
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
	}*/

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

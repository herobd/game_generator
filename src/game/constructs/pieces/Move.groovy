package game.constructs.pieces

import game.gdl.clauses.GDLClause
//import game.gdl.clauses.HasClausesWithDep
import game.gdl.clauses.base.BaseClause
import game.gdl.clauses.base.HasBaseClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.dynamic.HasDynCompClause
import game.gdl.clauses.legal.HasLegalClause
import game.gdl.clauses.legal.LegalClause
import game.gdl.statement.GDLStatement
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.statement.GameToken
import game.constructs.condition.Condition
import game.constructs.board.Board
import generator.FineTunable
import game.constructs.pieces.action.Action
import game.constructs.pieces.query.Query

import java.util.HashSet

/**
 * @author Lawrence Thatcher
 *
 * Contains the GDL descriptions of a legal move that can be performed by a piece
 */
class Move implements  FineTunable //HasDynCompClause, HasBaseClause, HasLegalClause, HasClausesWithDep
{
	/*private def inputs = []
	private DynamicComponentsClause dynComp
	private BaseClause base
	private LegalClause legal*/
	
	//private SimpleStatement input
	
	private List<List<Query>> preconditions=[]
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
	
	Move(List<List<Query>> preconditions, List<Action> postconditions)
	{
	    this.preconditions = preconditions
	    this.postconditions = postconditions
	}

    String getId() 
    {
        return id
    }
    void setId(String id)
    {
        this.id=id
        //input = new SimpleStatement("(<= (input ?p ("+id+" ?x ?y)) (index ?x) (index ?y) (role ?p))")
    }

	

	//@Override
	Collection<GDLClause> getGDLClauses(Board board,piece_id)
	{
		//return [base, dynComp, legal]
		def toRet=[compilePreconditions(board,piece_id),compilePostconditions(preconditions.size(),board,piece_id)]
	    return toRet
	}
	
	private GDLClause compilePreconditions(Board board, String piece_id)
	{
	    GString clause = GString.EMPTY
	    clause += "(<= (legal ${GameToken.PLAYER} (${id} "
	    for (int n=0; n<preconditions.size(); n++)
	        if (preconditions[n].size()>0)
	            clause += board.getSelectedSpaceGDL(n).join(' ')+" "
        clause +="))\n"
        //clause +="(role ?role)\n"
	    for (int n=0; n<preconditions.size(); n++)
	    {
	        for (Query q : preconditions[n])
	        {
	            clause += q.toGDL(board,piece_id,n) +"\n"
	        }
	    }
	    return new LegalClause([new GeneratorStatement(clause)])
	}
	//(<= (legal ?role (move ?x ?y ?u ?v))
    // (role ?role)
    // (coordinate ?u)
    // (coordinate ?v)...
	
	private GDLClause compilePostconditions(int numParams, Board board, String piece_id) //List<Actions> postconditions, String piece_id, String move_id
	{
	    Set< List<GString> > effectedSpaces=[];
	    Set<List<GString>> moveParams= new HashSet<GString>();
	    GString clause = GString.EMPTY
	    Map<GString,GString> cells = new HashMap<GString,GString>() //This map is to prevent a cell from getting two state assignments by allowing later postconditions to override previous ones 
	    Set<GString> definitions = new HashSet<GString>()
	    clause += "(<= (next "
	    for (int i=0; i<postconditions.size(); i++)
	    {
	        Action a = postconditions[i]
	        clause += a.effect(cells,board,piece_id,definitions)+"\n" //The effects of the postcondition
	        effectedSpaces.addAll(a.effected(board,definitions)) //the vaiables the effects use, like "?mTo", or possiblely adjcent identifiers
	        moveParams.addAll(a.params(board,definitions)) //the variables that the player selects
	    }
	    cells.each { cell, piece ->
            clause+="(cell "
            clause+=cell
            clause+=" "
            clause+=piece
            clause+=")\n"
        };
	    clause += board.getGeneralSpaceGDL() + ")\n" // general space is like "(cell ?m ?n ?var)"
	    //end next
	    
	    clause += "(does ${GameToken.PLAYER} (${id} "
	    for (List<String> space : moveParams)
	    {
	        clause+=space.join(" ")+" "
	    }
	    clause +="))\n"
	    
	    //define all the uneffected spaces to be the same
	    clause += "(true ${board.getGeneralSpaceGDL()})\n"
	    for (List<String> space : effectedSpaces)
	    {
	        if (space.size()==0) {
	            clause += "(distinct ${board.getGeneralSpaceGDLIndex(0)} ${space[0]})\n"
	        }
	        else
	        {
	            clause += "(or "
	            for (int i=0; i<space.size(); i++) //does 'or' support more than two parameters?
	            {
	                clause += "(distinct ${board.getGeneralSpaceGDLIndex(i)} ${space[i]})\n"
	            }
	            clause += ")"
	        }
	    }
	    clause+=definitions.join("\n")
	    clause+=")"
	    return new DynamicComponentsClause([new GeneratorStatement(clause)])
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
        int ret = 1
        for (List<Query> lq : preconditions)
        {
            for (Query q : lq)
                ret+=q.complexityCount()
        }
        for (Action a : postconditions) 
            ret+=a.complexityCount()
        return ret
    }
    
    @Override
    int getNumParams()
	{
	    int ret = 0 
	    for (List<Query> lq : preconditions)
        {
            for (Query q : lq)
                ret+=q.getNumParams()
        }
        for (Action a : postconditions) 
            ret+=a.getNumParams()
        return ret
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        int sofar=param
        
        for (List<Query> lq : preconditions)
        {
            for (Query q : lq)
            {
                if (sofar-q.getNumParams()<0)
                {
                    q.changeParam(sofar,amount)
                    return
                }
                sofar-=q.getNumParams()
            }
        }
        for (Action a : postconditions)
        {
            if (sofar-a.getNumParams()<0)
            {
                a.changeParam(sofar,amount)
                return
            }
            sofar-=a.getNumParams()
        }
        
    }
}

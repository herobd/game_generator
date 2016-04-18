package game.constructs.pieces

import game.constructs.pieces.action.Actions
import game.constructs.pieces.query.PieceOrigin
import game.constructs.pieces.query.Queries
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
import game.constructs.board.Board
import generator.CrossOver
import generator.FineTunable
import game.constructs.pieces.action.Action
import game.constructs.pieces.query.Query
import generator.Gene
import generator.Mutation

import java.util.HashSet

/**
 * @author Brain
 *
 * Contains the GDL descriptions of a legal move that can be performed by a piece
 */
class Move implements  FineTunable, Gene //HasDynCompClause, HasBaseClause, HasLegalClause, HasClausesWithDep
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
		// --Preconditions--

		//set whether has piece-origin or not
		this.preconditions = []
		if (RANDOM.nextBoolean())
			preconditions.add([new PieceOrigin()])
		else
			preconditions.add([])

		//add sections
		for (int i = 0; i < rand_positive(); i++)
			addPreconditionSection()


		// --Postconditions--
		for (int i = 0; i < rand_positive(); i++)
			addPostCondition()
		
		//for (Action a : postconditions)
		//{
		//    if (a in MoveToSelected)
		//}
	}
	
	Move(List<List<Query>> preconditions, List<Action> postconditions)
	{
	    this.preconditions = preconditions
	    this.postconditions = postconditions
	}
	
	static Move fromJSON(def parsed)
	{
	    def pre = []
	    parsed.preconditions.each { p ->
	        def level=[]
	        p.each { q ->
	            //level.push(grailsApplication.getArtefact("Domain",q.query)?.getClazz()?.fromJSON(q)) //aka, magic
	            level.push(Class.forName('game.constructs.pieces.query.'+q.query).fromJSON(q))
            }
            pre.push(level)
        }
        def post = []
        parsed.postconditions.each {p ->
            //post.push(grailsApplication.getArtefact("Domain",p.action)?.getClazz()?.fromJSON(p)) //aka, magic
            post.push(Class.forName('game.constructs.pieces.action.'+p.action).fromJSON(p))
        }
        return new Move(pre,post)
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
	Collection<GDLClause> getGDLClauses(Map<String,GDLClause> globalRules, Board board,piece_id)
	{   
	    GString moveParams = GString.EMPTY
	    for (int n=0; n<preconditions.size(); n++)
	        if (preconditions[n].size()>0)
	        {
	            moveParams += board.getSelectedSpaceGDL(n).join(' ')
	            moveParams += " "
            }
		//return [base, dynComp, legal]
		def toRet=[]
		toRet.addAll(compilePreconditions(globalRules,board,piece_id,moveParams))
		toRet.addAll(compilePostconditions(preconditions.size(),board,piece_id,moveParams))
	    return toRet
	}
	
	private List<GDLClause> compilePreconditions(Map<String,GDLClause> globalRules, Board board, String piece_id, GString moveParams)
	{
	    GString clause = GString.EMPTY
	    GString baseclause = GString.EMPTY
	    clause += "(<= (legal ${GameToken.PLAYER} (${id} "
	    baseclause += "(<= (input ?p (${id} "
	    clause += moveParams
	    baseclause += moveParams
        clause +="))\n"
        //clause +="(role ?p)\n"
        baseclause += ")) (role ?p) "
        
        clause += "\t(true (control ${GameToken.PLAYER}))\n"
	    for (int n=0; n<preconditions.size(); n++)
	    {
	        for (Query q : preconditions[n])
	        {
	            clause += "\t"
	            clause += q.toGDL(board,piece_id,n) 
	            clause +="\n"
	            q.setGlobalRules(globalRules,board)
	        }
	    }
	    for (int n=0; n<preconditions.size(); n++)
	        if (preconditions[n].size()>0)
	        {
	            for (String index : board.getSelectedSpaceGDL(n))
	                baseclause += "(index ${index}) "
            }
	    clause += ")"
	    baseclause += ")"
	    return [new LegalClause([new GeneratorStatement(clause)]),
	            new BaseClause([new SimpleStatement(baseclause)])]
	}
	//(<= (legal ?role (move ?x ?y ?u ?v))
    // (role ?role)
    // (coordinate ?u)
    // (coordinate ?v)...
	
	private List<GDLClause> compilePostconditions(int numParams, Board board, String piece_id, GString moveParams) //List<Actions> postconditions, String piece_id, String move_id
	{
	    Set< List<GString> > effectedSpaces=[];
	    //Set<List<GString>> moveParams= new HashSet<GString>();
	    GString clause = GString.EMPTY
	    Map<GString,GString> cells = new HashMap<GString,GString>() //This map is to prevent a cell from getting two state assignments by allowing later postconditions to override previous ones 
	    Set<GString> definitions = new HashSet<GString>()
	    
	    def ret =[]
	    
	    List<GString> otherEffects= new ArrayList<GString>()
	    for (Action a : postconditions)
	    {
	        otherEffects.push(a.effect(cells,board,piece_id,definitions)) //The effects of the postcondition
	        effectedSpaces.addAll(a.effected(board,definitions)) //the vaiables the effects use, like "?mTo", or possiblely adjcent identifiers
	        //moveParams.addAll(a.params(board,definitions)) //the variables that the player selects
	    }
	    cells.each { cell, piece ->
	        GString clauseCell = GString.EMPTY
	        clauseCell += "(<= (next (cell "
            clauseCell+=cell
            clauseCell+=" "
            clauseCell+=piece
            clauseCell+="))\n"
            clauseCell += "\t(does ${GameToken.PLAYER} (${id} "
	        clauseCell += moveParams
	        clauseCell +="))\n\t"
	        
	        clauseCell+=definitions.join("\n\t")
	        clauseCell+="\n)"
	        ret.push(new DynamicComponentsClause([new GeneratorStatement(clauseCell)]))
        };
        
        //Other effects, like score, game state
        for (GString ef : otherEffects)
        {
            if (ef.length()>0)
            {
                GString clauseOther = GString.EMPTY
	            clauseOther += "(<= (next "
	            clauseOther += ef
	            clauseOther+=")\n"
                clauseOther += "\t(does ${GameToken.PLAYER} (${id} "
	            clauseOther += moveParams
	            clauseOther +="))\n\t"
	            
	            clauseOther+=definitions.join("\n\t")
	            clauseOther+="\n)"
	            ret.push(new DynamicComponentsClause([new GeneratorStatement(clauseOther)]))
            }
        }
        
        //Permanance of all other spaces/pieces
        //define all the uneffected spaces to be the same
        GString clausePerm = GString.EMPTY
        clausePerm += "(<= (next "
	    clausePerm += board.getGeneralSpaceGDL() 
	    clausePerm += ")\n" // general space is like "(cell ?m ?n ?var)"
	    
	    
	    clausePerm += "\t(does ${GameToken.PLAYER} (${id} "
	    clausePerm += moveParams
	    clausePerm +="))\n"
	    
	    clausePerm += "\t(true ${board.getGeneralSpaceGDL()})\n"
	    for (List<String> space : effectedSpaces)
	    {
	        if (space.size()==0) {
	            clausePerm += "\t(distinct ${board.getGeneralSpaceGDLIndex(0)} ${space[0]})\n\t"
	        }
	        else
	        {
	            clausePerm += "\t(or \n"
	            for (int i=0; i<space.size(); i++) //does 'or' support more than two parameters?
	            {
	                clausePerm += "\t\t(distinct ${board.getGeneralSpaceGDLIndex(i)} ${space[i]})\n"
	            }
	            clausePerm += "\t)\n\t"
	        }
	    }
	    clausePerm+=definitions.join("\n\t")
	    clausePerm+="\n)"
	    ret.push(new DynamicComponentsClause([new GeneratorStatement(clausePerm)])) //This potentailly could use a (?role) (role ?role) instead of generator, but its safer this way
	    return ret
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

	List<List<Query>> getPreconditions()
	{
		return preconditions
	}

	List<Action> getPostconditions()
	{
		return postconditions
	}

	void addPreconditionSection()
	{
		int idx = RANDOM.nextInt(preconditions.size()+1)
        
        if (idx!=0)
        {
		    def section = []
		    def numPre = Math.max(1,(int) (Math.round(RANDOM.nextGaussian() * 1) + 1))
		    for (int i = 0; i < numPre; i++)
		    {
			    Query q = Queries.getRandomQuery(preconditions, idx)
			    section.add(q)
		    }
		    preconditions.add(idx, section)
	    }
	    else
	    {   
	        if (preconditions.size()==0)
	            preconditions.add(0, [new PieceOrigin()])
            else
                preconditions[0].push(new PieceOrigin())
	    }
		
	}

	void addPostCondition()
	{
		Action a = Actions.getRandomAction(preconditions)
		postconditions.add(a)
	}

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

	/**
	 * Provides a list of valid sections in a pre-conditions clause
	 * @return a list of indexes of each non-empty section
	 */
	static List<Integer> sections(List<List<Query>> preconditions)
	{
		def result = []
		for (int n = 0; n < preconditions.size(); n++)
		{
			if (preconditions[n].size() > 0)
				result.add(n)
		}
		return result
	}

	int rand_positive()
	{
		def a = RANDOM.nextGaussian()
		return Math.abs(a.intValue())+1
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

	@Override
	List<Mutation> getPossibleMutations()
	{
		def result = parameterMutations
		result += mutationMethod("addPreconditionSection")
		result += mutationMethod("addPostCondition")
		return result
	}

	@Override
	List<CrossOver> getPossibleCrossOvers(Gene other)
	{
		return []
	}

	@Override
	String toString()
	{
		String result = "{Pre:"
		result += preconditions.toString()
		result += " Post:"
		result += postconditions.toString()
		result += "}"
		return result
	}

	String convertToJSON()
    {
        def test=0
        List< String > acs = new ArrayList<String>()
        for (Action a : postconditions)
            acs.push(a.convertToJSON())
        
        List<String> qqs = []
        for (List<Query> qls : preconditions)
        {
            
            List<String> qs = []
            for (Query q : qls)
                qs.push(q.convertToJSON())
            
            qqs.push('['+qs.join(', ')+']')
        }
        
        return '{"preconditions": ['+qqs.join(', ')+'], "postconditions": ['+acs.join(', ')+']}'
    }
}

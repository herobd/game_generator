package game.constructs.pieces.query


import game.constructs.board.Board
import game.constructs.board.grid.SquareGrid
import game.constructs.pieces.query.Query
import game.gdl.clauses.GDLClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.statement.SimpleStatement

class IsNeighbor implements Query
{
    private int neighborOf=-1 //relative index to the space we are indicating this is a neighbor of
    private boolean nbors=true
    private boolean i_nbors=false
    IsNeighbor(int neighborOf)
    {
        if (neighborOf<0)
            this.neighborOf=neighborOf
    }
    
    IsNeighbor(int neighborOf, boolean nbors, boolean i_nbors)
    {
        if (neighborOf<0)
            this.neighborOf=neighborOf
        this.nbors=nbors
        this.i_nbors=i_nbors
    }
    
    @Override
    GString toGDL(Board board,String piece_id, int n)
    {
        //return "( true (cell ${board.getSelectedSpaceGDL(n).join(' ')} b))"
        
        
        GString ret = GString.EMPTY
        if (nbors&&i_nbors)
            ret+="(or("
        if (nbors)
            ret+= "(nbor ${board.getSelectedSpaceGDL(neighborOf+n).join(' ')} ${board.getSelectedSpaceGDL(n).join(' ')}) "
        if (i_nbors)
            ret+= "(i_nbor ${board.getSelectedSpaceGDL(neighborOf+n).join(' ')} ${board.getSelectedSpaceGDL(n).join(' ')})"
        if (nbors&&i_nbors)
            ret+="))"
        return ret
    }
    
    @Override 
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board)
    {
        
        if (board instanceof SquareGrid)
        {
            if (nbors && !globalRules.containsKey('nbors'))
            {
                globalRules['nbor']=getNborRuleSq()
            }
            if (i_nbors && !globalRules.containsKey('i_nbors'))
            {
                globalRules['i_nbor']=getI_NborRuleSq()
            }
        }
        else
        {
            throw new GroovyRuntimeException("IsNeighbor not implemented for board type")
        }
    }
    
    GDLClause getNborRuleSq()
	{
	    return new DynamicComponentsClause( [
	        new SimpleStatement("(<= (nbor ?n ?m ?othern ?otherm)\n\
	            \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(succ ?n ?othern) (?m ?otherm))"),
            new SimpleStatement("(<= (nbor ?n ?m ?othern ?otherm)\n\
                \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(succ ?othern ?n) (?m ?otherm))"),
            new SimpleStatement("(<= (nbor ?n ?m ?othern ?otherm)\n\
                \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(?n ?othern) (succ ?m ?otherm))"),
            new SimpleStatement("(<= (nbor ?n ?m ?othern ?otherm)\n\
                \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(?n ?othern) (succ ?otherm ?m))")
            ] );
            
	}
	
	GDLClause getI_NborRuleSq()
	{
	    return new DynamicComponentsClause( [
	        new SimpleStatement("(<= (i_nbor ?n ?m ?othern ?otherm)\n\
	            \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(succ ?n ?othern) (succ ?m ?otherm))"),
            new SimpleStatement("(<= (i_nbor ?n ?m ?othern ?otherm)\n\
                \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(succ ?othern ?n) (succ ?m ?otherm))"),
            new SimpleStatement("(<= (i_nbor ?n ?m ?othern ?otherm)\n\
                \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(succ ?othern ?n) (succ ?otherm ?m))"),
            new SimpleStatement("(<= (i_nbor ?n ?m ?othern ?otherm)\n\
                \t(index ?n) (index ?m) (index ?otherm) (index ?othern) \n\
	            \t(succ ?n ?othern) (succ ?otherm ?m))")
            ] );
            
	}
    
    int complexityCount()
    {
        return 1
    }
    
    @Override
    int getNumParams()
	{
        return 0
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        
    }
    
    @Override
    String convertToJSON()
    {
        return '{"query":"IsNeighbor", "neighborOf":'+neighborOf+', "nbors":"'+nbors+'", "i_nbors":"'+i_nbors+'"}'
    }
    
    //@Override
    static Query fromJSON(def parsed)
    {
        return new IsNeighbor(parsed.neighborOf, parsed.nbors, parsed.i_nbors)
    }
}

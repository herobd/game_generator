package game.constructs.pieces.query


import game.constructs.board.Board
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.functions.Function
import game.constructs.pieces.query.Query
import game.gdl.clauses.GDLClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.statement.SimpleStatement

class IsOpen implements Query
{

    @Override
    GString toGDL(Board board,String piece_id, int n)
    {
        return "(true (cell ${board.getSelectedSpaceGDL(n).join(' ')} b))"
    }
    
    @Override 
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board)
	{
		if (!globalRules.containsKey("open"))
		{
			globalRules["open"] = new DynamicComponentsClause([new SimpleStatement("(<= open (true (cell ?x ?y b)))")])
		}
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
	Collection<Function> getFunctions()
	{
		return []
	}

	@Override
	def getGDL_Signature()
	{
		return "open"
	}

    @Override
    String toString()
    {
        return "IsOpen"
    }
    
    @Override
    String convertToJSON()
    {
        return '{"query":"IsOpen"}'
    }
    
    //@Override
    static Query fromJSON(def parsed)
    {
        return new IsOpen()
    }
}

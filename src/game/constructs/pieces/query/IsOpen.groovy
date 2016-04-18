package game.constructs.pieces.query


import game.constructs.board.Board
import game.constructs.condition.functions.Function
import game.constructs.pieces.query.Query
import game.gdl.clauses.GDLClause

class IsOpen implements Query
{

    @Override
    GString toGDL(Board board,String piece_id, int n)
    {
        return "(true (cell ${board.getSelectedSpaceGDL(n).join(' ')} b))"
    }
    
    @Override 
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board) {}
    
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
		// TODO
		return null
	}
}

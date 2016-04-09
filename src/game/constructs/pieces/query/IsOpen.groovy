package game.constructs.pieces.query


import game.constructs.board.Board
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

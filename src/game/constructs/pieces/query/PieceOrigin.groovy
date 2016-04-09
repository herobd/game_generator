package game.constructs.pieces.query


import game.constructs.board.Board
import game.constructs.pieces.query.Query
import game.gdl.statement.GameToken
import game.gdl.clauses.GDLClause

class PieceOrigin implements Query
{

    @Override
    GString toGDL(Board board,String piece_id, int n)//This must occur only at level 0
    {
        if (n!=0)
            throw new GroovyRuntimeException("PieceOrigin must be a 0 layer preconditions, called as ?{n}")
        return "(true (cell ${board.getSelectedSpaceGDL(n).join(' ')} ${GameToken.PLAYER}_${piece_id}))"
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
        return '{"query":"PieceOrigin"}'
    }
}

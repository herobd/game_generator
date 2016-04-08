package game.constructs.pieces.query


import game.constructs.board.Board
import game.constructs.pieces.query.Query

class IsNeighbor implements Query
{

    @Override
    GString toGDL(Board board,String piece_id, int n)
    {
        //TODO return "(cell ${board.getSelectedSpaceGDL(n).join(' ')} b)"
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
}

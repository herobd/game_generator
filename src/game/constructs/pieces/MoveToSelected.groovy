package game.constructs.pieces

import game.constructs.board.Board
import game.constructs.pieces.Action
import game.gdl.statement.GameToken

class MoveToSelected implements Action
{
    @Override
    String effect(Board board, int n, String piece_id)//?selM ?selN
    {
        return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER}"+"_"+piece_id+") \
		        (cell "+board.getPieceSpaceGDL().join(' ')+" b)"
        
    }
    
    @Override
    List< List<String> > effected(Board board, int n)
    {
        return [board.getSelectedSpaceGDL(n), board.getPieceSpaceGDL()]
    }
    
    @Override
    Set< List<String> > params(Board board, int n)
    {
        return [board.getSelectedSpaceGDL(n), board.getPieceSpaceGDL()]
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

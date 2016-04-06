package game.constructs.pieces

import game.constructs.board.Board
import game.constructs.pieces.Action

class MoveToSelected extends Action
{
    @Override
    String effect(Board board, int n, String piece_id)//?selM ?selN
    {
        return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER_NAME}"+"_"+piece_id+") \
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

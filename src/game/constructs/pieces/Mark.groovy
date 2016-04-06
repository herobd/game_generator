package game.constructs.pieces


import game.constructs.board.Board
import game.constructs.pieces.Action

class Mark extends Action
{
    @Override
    String effect(Board board, int n, String piece_id)//?selM ?selN
    {
        return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER_NAME}"+"_"+piece_id+")"
        
    }
    
    @Override
    List< List<String> > effected(Board board, int n)
    {
        return [board.getSelectedSpaceGDL(n)]
    }
    
    @Override
    Set< List<String> > params(Board board, int n)
    {
        return [board.getSelectedSpaceGDL(n)]
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

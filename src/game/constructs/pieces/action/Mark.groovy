package game.constructs.pieces


import game.constructs.board.Board
import game.constructs.pieces.action.Action
import game.gdl.statement.GameToken

class Mark implements Action
{
    @Override
    String effect(Map cells,Board board, int n, String piece_id,Set<String> definitions)//?selM ?selN
    {
        //return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER}"+"_"+piece_id+")"
        cells[board.getSelectedSpaceGDL(n).join(' ')]="${GameToken.PLAYER}"+"_"+piece_id
    }
    
    @Override
    Set< List<String> > effected(Board board, int n,Set<String> definitions)
    {
        Set ret = new HashSet<List<String>>()
        ret.addAll([board.getSelectedSpaceGDL(n), board.getPieceSpaceGDL()])
        return ret
    }
    
    @Override
    Set< List<String> > params(Board board, int n,Set<String> definitions)
    {
        Set ret = new HashSet<List<String>>()
        ret.addAll([board.getSelectedSpaceGDL(n), board.getPieceSpaceGDL()])
        return ret
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

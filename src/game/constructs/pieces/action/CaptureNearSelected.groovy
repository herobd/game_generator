package game.constructs.pieces.action

import game.constructs.board.Board
import game.constructs.pieces.action.Action
import game.gdl.statement.GameToken

class CaptureNearSelected implements Action
{
    //private boolean friendlyFire
    private boolean i_nbors=false
    private boolean nbors=true
    
    
    
    @Override
    String effect(Map cells,Board board, int n, String piece_id,Set<String> definitions)//?selM ?selN
    {
        //return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER}"+"_"+piece_id+") \
		//        (cell "+board.getPieceSpaceGDL().join(' ')+" b)"
        if (nbors)
            for (List<String> space : board.getSelectedSpaceNborsGDL(n,definitions))
            {
                cells[space.join(' ')]="b"
            }
        if (i_nbors)
            for (List<String> space : board.getSelectedSpaceI_NborsGDL(n,definitions))
            {
                cells[space.join(' ')]="b"
            }
        //cells[board.getSelectedSpaceGDL(n).join(' ')+"<<nbors]="b"
        return ""
    }
    
    @Override
    List< List<String> > effected(Board board, int n,Set<String> definitions)
    {
        Set ret = new HashSet<List<String>>()
        if (nbors)
            ret.addAll(board.getSelectedSpaceNborsGDL(n,definitions))
        if (i_nbors)
            ret.addAll(board.getSelectedSpaceI_NborsGDL(n,definitions))
        return ret
    }
    
    @Override
    Set< List<String> > params(Board board, int n,Set<String> definitions)
    {
        Set ret = new HashSet<List<String>>()
        ret.add(board.getSelectedSpaceGDL(n))
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

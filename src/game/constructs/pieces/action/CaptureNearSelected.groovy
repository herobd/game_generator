package game.constructs.pieces.action

import game.constructs.board.Board
import game.constructs.pieces.action.Action
import game.gdl.statement.GameToken

class CaptureNearSelected implements Action
{
    //private boolean friendlyFire
    private boolean i_nbors=false
    private boolean nbors=true
    private int n=1
    Mark (int n)
    {
        if (n>0)
            this.n=n
    }
    
    Mark (int n, boolean nbors, boolean i_nbors)
    {
        if (n>0)
            this.n=n
        this.nbors=nbors
        this.i_nbors=i_nbors
    }
    
    
    @Override
    GString effect(Map<GString,GString> cells,Board board, String piece_id,Set<GString> definitions)//?selM ?selN
    {
        //return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER}"+"_"+piece_id+") \
		//        (cell "+board.getPieceSpaceGDL().join(' ')+" b)"
        if (nbors)
            for (List<String> space : board.getSelectedSpaceNborsGDL(n,definitions))
            {
                cells[space.join(" ")]="b"
            }
        if (i_nbors)
            for (List<String> space : board.getSelectedSpaceI_NborsGDL(n,definitions))
            {
                cells[space.join(" ")]="b"
            }
        //cells[board.getSelectedSpaceGDL(n).join(' ')+"<<nbors]="b"
        return GString.EMPTY
    }
    
    @Override
    List< List<GString> > effected(Board board,Set<GString> definitions)
    {
        Set ret = new HashSet<List<GString>>()
        if (nbors)
            ret.addAll(board.getSelectedSpaceNborsGDL(n,definitions))
        if (i_nbors)
            ret.addAll(board.getSelectedSpaceI_NborsGDL(n,definitions))
        return ret
    }
    
    @Override
    Set< List<GString> > params(Board board,Set<GString> definitions)
    {
        Set ret = new HashSet<List<GString>>()
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

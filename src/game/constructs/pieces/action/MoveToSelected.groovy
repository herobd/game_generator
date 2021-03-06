package game.constructs.pieces.action

import game.constructs.board.Board
import game.constructs.pieces.action.Action
import game.gdl.statement.GameToken

class MoveToSelected implements Action
{
    private int n=1
    MoveToSelected (int n)
    {
        if (n>0)
            this.n=n
    }
    @Override
    GString effect(Map<GString,GString> cells,Board board, String piece_id,Set<GString> definitions) //?selM ?selN
    {
        
        cells[board.getSelectedSpaceGDL(n).join(" ")]="${GameToken.PLAYER}_${piece_id}"
        cells[board.getPieceSpaceGDL().join(" ")]="b"
        return GString.EMPTY
    }
    
    @Override
    Set< List<GString> > effected(Board board, Set<GString> definitions)
    {
        Set ret = new HashSet<List<GString>>()
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

    @Override
    String toString()
    {
        return "Move(" + Integer.toString(n) + ")"
    }
    
    @Override
    String convertToJSON()
    {
        return '{"action":"MoveToSelected", "parameter":'+n+'}'
    }
    
    //@Override
    static Action fromJSON(def parsed)
    {
        return new MoveToSelected(parsed.parameter)
    }
}

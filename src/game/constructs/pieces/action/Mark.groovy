package game.constructs.pieces.action


import game.constructs.board.Board
import game.constructs.pieces.action.Action
import game.gdl.statement.GameToken

class Mark implements Action
{
    private int n=1
    Mark (int n)
    {
        if (n>0)
            this.n=n
    }

    @Override
    GString effect(Map<GString,GString> cells,Board board, String piece_id,Set<GString> definitions)//?selM ?selN
    {
        //return "(cell "+board.getSelectedSpaceGDL(n).join(' ')+" ${GameToken.PLAYER}"+"_"+piece_id+")"
        cells[board.getSelectedSpaceGDL(n).join(" ")]="${GameToken.PLAYER}_${piece_id}"
        return GString.EMPTY
    }
    
    @Override
    Set< List<GString> > effected(Board board, Set<GString> definitions)
    {
        Set ret = new HashSet<List<String>>()
        ret.addAll([board.getSelectedSpaceGDL(n)])
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
        return "Mark(" + Integer.toString(n) + ")"
    }
    
    @Override
    String convertToJSON()
    {
        return '{"action":"Mark", "parameter":'+n+'}'
    }
    
    //@Override
    static Action fromJSON(def parsed)
    {
        return new Mark(parsed.parameter)
    }
}

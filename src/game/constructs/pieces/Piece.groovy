package game.constructs.pieces

import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import generator.FineTunable

/**
 * @author Lawrence Thatcher
 *
 * An abstract representation of a game piece.
 */
class Piece implements HasClauses, FineTunable
{
	private String name = ""	//perhaps change to type later..?
	private Placement placement
	private List<Move> moves

	Piece(String name, Placement placement, List<Move> moves)
	{
		this.name = name
		this.placement = placement
		this.moves = moves
	}

	Piece(Placement placement, List<Move> moves)
	{
		this.placement = placement
		this.moves = moves
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		def clauses = []
		clauses += placement.GDLClauses
		for (Move m : moves)
			clauses += m.GDLClauses
		return clauses
	}

	@Override
	Piece clone()
	{
		return new Piece(name, placement, new ArrayList<Move>(moves))
	}

    int complexityCount()
    {
        int ret=0
        for (Move move : moves)
        {
            ret+=1
            ret += move.complexityCount()
        }
        return ret
    }
    
    @Override
    int getNumParams()
	{
	    
	    int ret=0
	    //TODO, initail position?
	    
        for (Move move : moves)
        {
            ret += move.getNumParams()
        }
        return ret
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        int sofar=param
        for (Move move : moves)
        {
            if (sofar-move.getNumParams()<0)
            {
                move.changeParam(sofar,amount)
                return
            }
            else
                sofar-=move.getNumParams()
        }
    }
}

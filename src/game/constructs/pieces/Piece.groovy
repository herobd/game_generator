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
	private StartingPositions startPositions
	
	//private MoveType moveType
	private List<Move> moves
	private List<Piece> children =[]

	Piece(String name, StartingPositions startPositions, List<Move> moves)
	{
		this.name = name
		this.startPosition = startPosition
		this.moves = moves
	}

	Piece(StartingPosition startPositions, List<Move> moves)
	{
		this.startPosition = startPositions
		this.moves = moves
		nameMoves()
	}
	
	private void nameMoves()
	{
	    for (int i=0; i<moves.length; i++)
		{
		    this.moves[i].setId(name+'_m'+i)
		}
	}
	
	void removeMove(int i)
	{
	    moves.remove(i)
	    nameMoves()
	}
	
	void addMove(Move m)
	{
	    this.moves.push(m)
	    nameMoves()
	}
	
	Move getMove(int i)
	{
	    return this.moves[i]
	}
	
	String getName()
	{
	    return name;
    }
    
    String setName(String name)
    {
        this.name=name;
    }

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		def clauses = []
		clauses += startPositions.GDLClauses //?
		for (Move m : moves)
			clauses += m.GDLClauses
		return clauses
	}

	@Override
	Piece clone()
	{
		return new Piece(name, startPositions, new ArrayList<Move>(moves))
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
	    
	    int ret=startPositions.getNumParams()
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
        if (sofar-startPositions.getNumParams()<0)
        {
            startPositions.changeParam(sofar,amount)
            return
        }
        sofar-=startPositions.getNumParams()
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

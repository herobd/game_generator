package game.constructs.pieces

import game.gdl.clauses.GDLClause
//import game.gdl.clauses.HasClausesWithDep
import game.gdl.clauses.base.BaseClause
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.GameToken
import generator.FineTunable
import game.constructs.board.Board
import game.constructs.pieces.StartingPosition

/**
 * @author Lawrence Thatcher
 *
 * An abstract representation of a game piece.
 */
class Piece implements  FineTunable //HasClausesWithDep
{
	private String name = ""	//perhaps change to type later..?
	private List<StartingPosition> startPositions
	
	//private MoveType moveType
	private List<Move> moves
	//private List<Piece> children =[]

	Piece(String name, List<StartingPosition> startPositions, List<Move> moves)
	{
		this.name = name
		this.startPosition = startPosition
		this.moves = moves
	}

	Piece(List<StartingPosition> startPositions, List<Move> moves)
	{
		this.startPositions = startPositions
		this.moves = moves
		nameMoves()
	}
	
	Piece(StartingPosition startPosition, Move move)
	{
		this.startPositions = [startPosition]
		this.moves = [move]
		nameMoves()
	}
	
	Piece(List<Move> moves)
	{
		this.startPositions = [new StartingPosition()]
		this.moves = moves
		nameMoves()
	}
	
	Piece(Move move)
	{
		this.startPositions = [new StartingPosition()]
		this.moves = [move]
		nameMoves()
	}
	
	private void nameMoves()
	{
	    for (int i=0; i<moves.size(); i++)
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
    
    String getName(String player)
	{
	    return player+'_'+name;
    }
    
    void setName(String name)
    {
        this.name=name
        nameMoves()
    }
    
    List<StartingPosition> getStartPositions()
	{
	    return startPositions;
    }
    
    void setStartPositions(List<StartingPosition> startPositions)
    {
        this.startPositions=startPositions;
    }

	//@Override
	/**
	 *
	 * @param globalRules The global rules mapped by name to prevent duplications
	 * @return The cluases needed to describe this piece
	 */
	Collection<GDLClause> getGDLClauses(Map<String,GDLClause> globalRules, Board board)
	{
		def clauses = [new BaseClause([new GeneratorStatement("(${name} ${GameToken.PLAYER}_${name})")]),
		               new BaseClause([new GeneratorStatement("(${GameToken.PLAYER_MARK} ${GameToken.PLAYER}_${name})")])]
		//clauses += startPositions.GDLClauses //These are accepted by the board eariler
		for (Move m : moves)
			clauses += m.getGDLClauses(globalRules,board,name)
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
        for (StartingPosition sp : startPositions)
        {
            ret += sp.getNumParams()
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
        for (StartingPosition sp : startPositions)
        {
            if (sofar-sp.getNumParams()<0)
            {
                sp.changeParam(sofar,amount)
                return
            }
            else
                sofar-=sp.getNumParams()
        }
    }
    
    String convertToJSON()
    {
        
        List<String> ms = []
		for (Move move : moves)
		{
			ms.push(move.convertToJSON())
		}
		List<String> ss = []
		for (StartingPosition s : startPositions)
		{
			ss.push(s.convertToJSON())
		}
		String ret='{"moves": ['+ms.join(', ')+'], "startPositions": ['+ss.join(', ')+'] }'
    }
}

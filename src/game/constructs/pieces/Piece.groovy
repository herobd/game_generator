package game.constructs.pieces

import game.gdl.clauses.GDLClause
//import game.gdl.clauses.HasClausesWithDep
import game.gdl.clauses.base.BaseClause
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.GameToken
import generator.CrossOver
import generator.FineTunable
import game.constructs.board.Board
import game.constructs.pieces.StartingPosition
import generator.Gene
import generator.Mutation
import generator.NestedCrossOver

/**
 * @author Lawrence Thatcher
 *
 * An abstract representation of a game piece.
 */
class Piece implements  FineTunable, Gene //HasClausesWithDep
{
	private String name = ""	//perhaps change to type later..?
	private List<StartingPosition> startPositions
	
	//private MoveType moveType
	private List<Move> moves
	//private List<Piece> children =[]

	Piece(String name, List<StartingPosition> startPositions, List<Move> moves)
	{
		this.name = name
		this.startPositions = startPositions
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

	Piece()
	{
		this.startPositions = [new StartingPosition()]
		this.moves = [new Move()]
		nameMoves()
	}
	
	static Piece fromJSON(def parsed)
	{
	    def startPositions =[]
	    parsed.startPositions.each { sp ->
	        startPositions.push(StartingPosition.fromJSON(sp))
        }
        def moves =[]
	    parsed.moves.each { m ->
	        moves.push(Move.fromJSON(m))
        }
        return new Piece(startPositions,moves)
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
		int ret = 0
		for (StartingPosition sp : startPositions)
		{
			ret += sp.getNumParams()
		}
        for (Move move : moves)
        {
            ret += move.getNumParams()
        }

        return ret
	}
	
	@Override
    void changeParam(int param, int amount)
    {
		int i = 0
		for (StartingPosition sp : startPositions)
		{
			if (param >= i+sp.numParams)
			{
				i += sp.numParams
			}
			else //within range
			{
				int idx = param - i
				sp.changeParam(idx, amount)
			}
		}
		for (Move m : moves)
		{
			if (param >= i+m.numParams)
			{
				i += m.numParams
			}
			else //within range
			{
				int idx = param - i
				m.changeParam(idx, amount)
			}
		}

//        int sofar=param
//        if (sofar-startPositions.getNumParams()<0)
//        {
//            startPositions.changeParam(sofar,amount)
//            return
//        }
//        sofar-=startPositions.getNumParams()
//        for (Move move : moves)
//        {
//            if (sofar-move.getNumParams()<0)
//            {
//                move.changeParam(sofar,amount)
//                return
//            }
//            else
//                sofar-=move.getNumParams()
//        }
//        for (StartingPosition sp : startPositions)
//        {
//            if (sofar-sp.getNumParams()<0)
//            {
//                sp.changeParam(sofar,amount)
//                return
//            }
//            else
//                sofar-=sp.getNumParams()
//        }
    }

	def addRandomStartPosition()
	{
		startPositions.add(new StartingPosition())
	}

	def addMove()
	{
		moves.add(new Move())
	}

	def removeRandomStartPosition()
	{
		if (startPositions.size() > 1)
		{
			int idx = RANDOM.nextInt(startPositions.size())
			startPositions.removeAt(idx)
		}
	}

	@Override
	List<Mutation> getPossibleMutations()
	{
		def result = getParameterMutations()

		for (StartingPosition sp : startPositions)
			result.addAll(sp.possibleMutations)
		for (Move m : moves)
			result.addAll(m.possibleMutations)

		if (startPositions.size() > 1)
			result.add(mutationMethod("removeRandomStartPosition"))
		result.add(mutationMethod("addRandomStartPosition"))
		result.add(mutationMethod("addMove"))
		return result
	}

	@Override
	List<CrossOver> getPossibleCrossOvers(Gene other)
	{
		other = other as Piece
		List<CrossOver> result = []
		// Start-Position Individual Cross Overs
		result.add(new NestedCrossOver(this.startPositions, other.startPositions))
		// All Start-Position Cross Over
		def c = {Piece p -> this.startPositions = p.startPositions}
		c.curry(other)
		result.add(new CrossOver(c))
		// All Moves Cross Over
		c = {Piece p -> this.moves = p.moves}
		c.curry(other)
		result.add(new CrossOver(c))

		return result
	}

	@Override
	String toString()
	{
		// Starting Positions
		String result = "Starting Positions: ["
		for (def sp : startPositions)
		{
			result += sp.toString() + ","
		}
		result = result.substring(0, result.length()-1)
		result += "] "
		// Moves
		result += "Moves: ["
		for (def m : moves)
		{
			result += m.toString() + ","
		}
		result = result.substring(0, result.length()-1)
		result += "]"

		return result
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
		return ret
    }
}

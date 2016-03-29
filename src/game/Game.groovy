package game

import game.constructs.condition.Conditional
import game.constructs.player.Players
import game.constructs.TurnOrder
import game.constructs.board.Board
import game.constructs.condition.TerminalConditional
import game.constructs.end.EndGameConditions
import game.constructs.pieces.NamedPieces
import game.constructs.pieces.Piece
import game.gdl.clauses.GDLClause
import game.gdl.GDLConvertable
import game.gdl.GDLDescription
import generator.Evolvable
import generator.Gene
import generator.FineTunable

/**
 * @author Lawrence Thatcher
 *
 * A class holding an abstract game description of a particular game.
 */
class Game implements Evolvable, GDLConvertable, FineTunable
{
	private static final double DEFAULT_CROSS_OVER_PROBABILITY = 0.1

	private String name = "unnamed game"
	private String id = "no_id"
	private Players players
	private Board board
	private TurnOrder turnOrder
	private List<Piece> pieces
	private EndGameConditions end
	
	private List<Evolvable> parents = []

	/**
	 *
	 * @param players a Players object, representing the players in the game
	 * @param board a Board object representing the game's board
	 * @param turnOrder the TurnOrder value, representing the turn-order pattern that will be followed in this game
	 * @param pieces a list of Piece objects representing the game pieces that will be used in this game.
	 * If none are specified, it will default to simple mark-and-place pieces, similar to the Ludi system.
	 * @param end a list of TerminalConditions that will be used to set when the game will end,
	 * as well as who the winner will be (if any).
	 */
	Game(Players players, Board board, TurnOrder turnOrder, List<Piece> pieces, List<TerminalConditional> end)
	{
		this.players = players
		this.board = board
		this.turnOrder = turnOrder
		if (pieces == null || pieces == [])
			this.pieces = [NamedPieces.DEFAULT_PIECE]
		else
			this.pieces = pieces
		this.end = new EndGameConditions(end, board)
	}
	
	int getNumPlayers()
	{
	    return players.size()
	}
	
	String getId()
	{
	    return id
	}
	
	void setId(String id)
	{
	    this.id=id
	}
	
	String getName()
	{
	    return name
	}
	
	int getGDLVersion()
	{
	    return 1
	}

	@Override
	GDLDescription convertToGDL()
	{
		List<GDLClause> clauses = []
		clauses += players.GDLClauses
		clauses += board.GDLClauses
		clauses += turnOrder.GDLClauses
		for (Piece p : pieces)
		{
			clauses += p.GDLClauses
		}
		clauses += end.supportedBoardGDLClauses
		clauses += end.GDLClauses

		GameContextInfo contextInfo = new GameContextInfo(players)
		return new GDLDescription(name, clauses, contextInfo)
	}

	@Override
	Evolvable crossOver(Evolvable mate)
	{
		Game child = this.clone()
		child.setParents(this,mate)
		List<List<Gene>> matchableGenes = [child.genes, mate.genes].transpose()
		for (def pair : matchableGenes)
		{
			Gene m = pair[0]
			Gene f = pair[1]
			m.crossOver(f)
		}
		return child
	}
	
	void setParents(Evolvable temp, Evolvable other)
	{
	    parents.add(temp)
	    parents.add(other)
	}
	
	@Override
	List<Evolvable> getParents()
	{
	    return parents
	}

	@Override
	def mutate()
	{
		for (Gene gene : genes)
		{
			gene.mutate()
		}
	}

	@Override
	Game clone()
	{
		Players c_players = players.clone()
		Board c_board = (Board)board.clone()
		def c_pieces = []
		def c_end = []
		for (Piece p : pieces)
			c_pieces.add(p.clone())
		for (Conditional c : end.conditionals)
			c_end.add(c.clone())
		return new Game(c_players, c_board, turnOrder, c_pieces, c_end)
	}

	@Override
	List<Gene> getGenes()
	{
		return [players]
	}

	@Override
	String toString()
	{
		String result = ""
		result += "Players: " + players.toString() + "\n"
		result += "Board: " + board.toString() + "\n"
		result += "TurnOrder: " + turnOrder.toString() + "\n"
		result += "Pieces: " + pieces.toString() + "\n"
		result += "End:\n"
		for (Conditional c : end.conditionals)
		{
			result += "\t" + c.toString() + "\n"
		}
		return result
	}

    int complexityCount_piece()
    {
        int ret=0
        for (Piece p : pieces)
            ret += 1 + p.complexityCount()
        return ret
    }
    int complexityCount_end()
    {
        int ret=0
        for (Conditional c : end.conditionals)
            ret += 1 + c.complexityCount()
        return ret
    }
    
    @Override
    int getNumParams()
    {
        def ret=0
        ret+=players.getNumParams();
        ret+=board.getNumParams();
        ret+=turnOrder.getNumParams();
        for (Piece p : pieces)
            ret+=p.getNumParams();
        ret+=end.getNumParams();
        return ret
    }
    
    @Override
    void changeParam(int param, int amount)
    {
        int sofar=param
        if (sofar-players.getNumParams()<0)
        {
            players.changeParam(sofar,amount)
            return
        }
        else
            sofar-=players.getNumParams()
        
        if (sofar-board.getNumParams()<0)
        {
            board.changeParam(sofar,amount)
            return
        }
        else
            sofar-=board.getNumParams()
            
        if (sofar-turnOrder.getNumParams()<0)
        {
            turnOrder.changeParam(sofar,amount)
            return
        }
        else
            sofar-=turnOrder.getNumParams()
        
        for (Piece p : pieces)
        {
            if (sofar-p.getNumParams()<0)
            {
                p.changeParam(sofar,amount)
                return
            }
            else
                sofar-=p.getNumParams()
        }
        
        if (sofar-end.getNumParams()<0)
        {
            end.changeParam(sofar,amount)
            return
        }
        else
            sofar-=end.getNumParams()
    }
}

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

/**
 * @author Lawrence Thatcher
 *
 * A class holding an abstract game description of a particular game.
 */
class Game implements Evolvable, GDLConvertable
{
	private static final double DEFAULT_CROSS_OVER_PROBABILITY = 0.1

	private Players players
	private Board board
	private TurnOrder turnOrder
	private List<Piece> pieces
	private EndGameConditions end

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
	
	int getId()
	{
	    return 'no_id'
	}
	
	int getName()
	{
	    return 'no_name'
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

		return new GDLDescription(clauses)
	}

	@Override
	Evolvable crossOver(Evolvable mate)
	{
		Game child = this.clone()
		List<List<Gene>> matchableGenes = [child.genes, mate.genes].transpose()
		for (def pair : matchableGenes)
		{
			Gene m = pair[0]
			Gene f = pair[1]
			m.crossOver(f)
		}
		return child
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
}

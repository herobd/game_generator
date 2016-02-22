import constructs.Players
import constructs.TurnOrder
import constructs.board.Board
import constructs.condition.TerminalCondition
import constructs.end.EndGameConditions
import constructs.pieces.NamedPieces
import constructs.pieces.Piece
import gdl.clauses.GDLClause
import gdl.GDLConvertable
import gdl.GDLDescription

/**
 * @author Lawrence Thatcher
 *
 * A class representing the abstract game description of a particular game.
 */
class GameDescription implements GDLConvertable
{
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
	GameDescription(Players players, Board board, TurnOrder turnOrder, List<Piece> pieces, List<TerminalCondition> end)
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
		clauses += end.GDLClauses

		return new GDLDescription(clauses)
	}
}

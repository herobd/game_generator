import constructs.Players
import constructs.TurnOrder
import constructs.board.Board
import constructs.end.EndGameConditions
import constructs.pieces.Piece
import gdl.GDLClause
import gdl.GDLDescription

/**
 * @author Lawrence Thatcher
 */
class GameDescription implements GDLConvertable
{
	private Players players
	private Board board
	private TurnOrder turnOrder
	private List<Piece> pieces
	private EndGameConditions end

	GameDescription(Players players, Board board, TurnOrder turnOrder, List<Piece> pieces, EndGameConditions end)
	{
		this.players = players
		this.board = board
		this.turnOrder = turnOrder
		this.pieces = pieces
		this.end = end
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

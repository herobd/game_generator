package game
/**
 * @author Lawrence Thatcher
 */
import game.Game
import game.constructs.TurnOrder
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.NegatedCondition
import game.constructs.condition.TerminalConditional
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.result.EndGameResult
import game.constructs.player.Players
import game.constructs.pieces.Piece
import game.constructs.pieces.Move
import game.constructs.pieces.action.Mark
import game.constructs.pieces.action.MoveToSelected
import game.constructs.pieces.query.IsOpen
import game.constructs.pieces.query.PieceOrigin
import game.constructs.pieces.StartingPosition
import game.gdl.GDLDescription


class GenerateGameMain
{
	public static void main(String[] args)
	{
		def board = new SquareGrid(4, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow([3]), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Move mark = new Move([[],[new IsOpen()]],[new Mark(1)]);
		Move move = new Move([[new PieceOrigin()],[new IsOpen()]],[new MoveToSelected(1)]);
		Piece basic = new Piece([new StartingPosition(2)],[mark]);
		Piece starter = new Piece([new StartingPosition(StartingPosition.PositionType.Center,1)],[move]);
		Game ticTacToe = new Game(new Players(["Red", "Black", "Blue"]), board, TurnOrder.Alternating, [basic,starter], end)
		GDLDescription gdl = ticTacToe.convertToGDL()
		println gdl.toString()
	}
}

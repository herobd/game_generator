package game
/**
 * @author Lawrence Thatcher
 */
import game.constructs.TurnOrder
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.NegatedCondition
import game.constructs.condition.TerminalConditional
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.result.EndGameResult
import game.constructs.player.Players
import game.gdl.GDLDescription

class GenerateGameMain
{
	public static void main(String[] args)
	{
		def board = new SquareGrid(4, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Game ticTacToe = new Game(new Players(["Red", "Black", "Blue"]), board, TurnOrder.Alternating, [], end)
		GDLDescription gdl = ticTacToe.convertToGDL()
		println gdl.toString()
	}
}

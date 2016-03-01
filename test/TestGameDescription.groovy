import constructs.player.Players
import constructs.TurnOrder
import constructs.board.grid.SquareGrid
import constructs.condition.NegatedCondition
import constructs.condition.TerminalConditional
import constructs.condition.functions.GameFunction
import constructs.condition.result.EndGameResult
import gdl.GDLDescription
import gdl.clauses.AbstractClause
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestGameDescription
{
	@Test
	void test_convertToGDL()
	{
		def board = new SquareGrid(3, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Game ticTacToe = new Game(new Players(["White", "Black"]), board, TurnOrder.Alternating, [], end)
		GDLDescription gdl = ticTacToe.convertToGDL()
		//Roles
		def roles = (AbstractClause)gdl.rolesClauses[0]
		assert roles.contains("(role White)")
		assert roles.contains("(role Black)")

		println gdl.toString()
	}

	@Test
	void test_evolution()
	{
		def players = new Players(["White", "Black", "Salmon", "Pink"])
		def board = new SquareGrid(3, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Game p1 = new Game(players, board, TurnOrder.Alternating, [], end)

		players = new Players(["Red", "Orange", "Green", "Yellow", "Gold"])
		Game p2 = new Game(players, board, TurnOrder.Alternating, [], end)

		Game p3
		for (int i = 0; i < 20; i++)
		{
			p3 = p1.crossOver(p2) as Game
			p3.mutate()
			println p3.toString()
			println "\n"
		}
	}
}

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
	void testConvertToGDL()
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

		System.out.println(gdl.toString())
	}
}

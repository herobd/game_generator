import constructs.Players
import constructs.TurnOrder
import constructs.board.grid.SquareGrid
import constructs.end.EndGameConditions
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
		def end = new EndGameConditions([])
		GameDescription ticTacToe = new GameDescription(new Players(["White", "Black"]), board, TurnOrder.Alternating, [], end)
		GDLDescription gdl = ticTacToe.convertToGDL()
		//Roles
		def roles = (AbstractClause)gdl.rolesClauses[0]
		assert roles.contains("(role White)")
		assert roles.contains("(role Black)")

		System.out.println(gdl.toString())
	}
}

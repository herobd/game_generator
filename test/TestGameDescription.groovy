import constructs.Players
import constructs.TurnOrder
import constructs.board.Board
import constructs.end.EndGameConditions
import gdl.GDLDescription
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestGameDescription
{
	@Test
	void testConvertToGDL()
	{
		GameDescription ticTacToe = new GameDescription(new Players(["White", "Black"]), new Board(), TurnOrder.Alternating, [], new EndGameConditions())
		GDLDescription gdl = ticTacToe.convertToGDL()
		System.out.println(gdl.toString())
	}
}

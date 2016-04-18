package game.constructs.board

import game.constructs.board.grid.SquareGrid
import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestSquareGrid
{
	private SquareGrid sg1
	private SquareGrid sg2

	@Before
	void setup()
	{
		sg1 = new SquareGrid(3, true)
		sg2 = new SquareGrid(5, false)
	}

	@Test
	void test_getPossibleMutations()
	{
		def M = sg1.possibleMutations
		assert M.size() == 2
		assert M[0].toString().contains("changeParam")
		assert M[1].toString() == "toggle_iNbors"
	}
}

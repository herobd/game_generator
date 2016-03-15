package game.constructs.board

import game.constructs.board.grid.SquareGrid
import game.constructs.condition.functions.GameFunction
import game.gdl.clauses.dynamic.DynamicComponentsClause
import org.junit.Test
import static org.junit.Assert.fail

/**
 * @author Lawrence Thatcher
 *
 * Tests basic methods of the Board class, and some if it's child classes
 */
class TestBoard
{
	@Test
	void test_getImplementation()
	{
		// non-parametrized
		def board = new SquareGrid(3)
		def gdl = board.getImplementation(GameFunction.Open)
		assert gdl instanceof DynamicComponentsClause
		assert gdl.contains("(<= open\n(true (cell ?x ?y b)))")

		// parametrized
		gdl = board.getImplementation(GameFunction.N_inARow(3))
		assert gdl instanceof DynamicComponentsClause
		assert gdl.contains("(<= (3inARow ?w) (row3 ?x ?y ?w))")
		assert gdl.statements.size() == 4

	}

	@Test
	void test_getImplementation__throwsException()
	{
		try
		{
			def board = new Board()
			board.getImplementation(GameFunction.N_inARow(5))
			fail("The default board does not support the in-a-row function")
		}
		catch (FunctionNotSupportedException ignore)
		{}
	}
}

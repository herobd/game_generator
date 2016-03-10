package game.constructs.board

import game.constructs.board.grid.Increment
import game.constructs.board.grid.Index
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Tests for the Increment helper class
 */
class TestIncrement
{
	@Test
	void test_call__Ascending()
	{
		def x = new Index("x")
		def inc = Increment.Ascending
		assert x.value == 0
		inc(x)
		assert x.value == 1
		inc(x)
		assert x.value == 2
	}

	@Test
	void test_call__Descending()
	{
		def y = new Index("y", 3)
		def inc = Increment.Descending
		assert y.value == 3
		inc(y)
		assert y.value == 2
		inc(y)
		assert y.value == 1
	}

	@Test
	void test_call__None()
	{
		def z = new Index("z")
		def inc = Increment.None
		assert z.value == 0
		inc(z)
		assert z.value == 0
		inc(z)
		assert z.value == 0
	}
}

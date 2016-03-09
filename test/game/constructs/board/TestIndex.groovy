package game.constructs.board

import game.constructs.board.grid.Index
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Contains tests for the Index class.
 */
class TestIndex
{
	@Test
	void test_toString()
	{
		def x = new Index("x")
		assert x.toString() == "?x"
		x = new Index("x", 1)
		assert x.toString() == "?x1"
		x = new Index("x", 2)
		assert x.toString() == "?x2"
	}

	@Test
	void test_next()
	{
		def x = new Index("x")
		assert x.toString() == "?x"
		x++
		assert x instanceof Index
		assert x.toString() == "?x1"
		x++
		assert x.toString() == "?x2"
	}

	@Test
	void test_previous()
	{
		def x = new Index("x", 2)
		assert x.toString() == "?x2"
		x--
		assert x instanceof Index
		assert x.toString() == "?x1"
		x--
		assert x.toString() == "?x"
	}

	@Test
	void test_GString()
	{
		def x = new Index("x")
		String result = "value 0: ${x}"
		x++
		result += "\nvalue 1: ${x}"
		x++
		result += "\nvalue 2: ${x}"
		println result
		assert result == "value 0: ?x\nvalue 1: ?x1\nvalue 2: ?x2"
	}
}

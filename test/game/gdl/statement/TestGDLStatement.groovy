package game.gdl.statement

import game.gdl.statement.SimpleStatement
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestGDLStatement
{
	private static final SimpleStatement gdl_singleLine = new SimpleStatement("(<= (input ?p noop) (role ?p))")


	private static final SimpleStatement gdl_multiLine = new SimpleStatement(
			"(<= (next (control Black))\n" +
			"(true (control White)))")

	@Test
	void testText()
	{
		SimpleStatement foo = new SimpleStatement("(<= (input ?p noop) (role ?p))")
		assert foo.text == "(<= (input ?p noop) (role ?p))"
	}

	@Test
	void testText_multiLine()
	{
		SimpleStatement foo = new SimpleStatement(
				"(<= (next (control Black))\n" +
				"(true (control White)))")
		assert foo.text == "(<= (next (control Black))\n(true (control White)))"
	}

	@Test
	void testText_static()
	{
		assert gdl_singleLine.text == "(<= (input ?p noop) (role ?p))"
		assert gdl_multiLine.text == "(<= (next (control Black))\n(true (control White)))"
	}

	@Test
	void testToString()
	{
		SimpleStatement foo = new SimpleStatement("(<= (input ?p noop) (role ?p))")
		assert foo.toString() == "(<= (input ?p noop) (role ?p))"
	}

	@Test
	void testToString_static()
	{
		assert gdl_singleLine.toString() == "(<= (input ?p noop) (role ?p))"
		assert gdl_multiLine.toString() == "(<= (next (control Black))\n(true (control White)))"
	}

}

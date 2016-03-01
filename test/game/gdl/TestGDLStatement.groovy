package game.gdl

import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestGDLStatement
{
	private static final GDLStatement gdl_singleLine = new GDLStatement("(<= (input ?p noop) (role ?p))")


	private static final GDLStatement gdl_multiLine = new GDLStatement(
			"(<= (next (control Black))\n" +
			"(true (control White)))")

	@Test
	void testText()
	{
		GDLStatement foo = new GDLStatement("(<= (input ?p noop) (role ?p))")
		assert foo.text == "(<= (input ?p noop) (role ?p))"
	}

	@Test
	void testText_multiLine()
	{
		GDLStatement foo = new GDLStatement(
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
		GDLStatement foo = new GDLStatement("(<= (input ?p noop) (role ?p))")
		assert foo.toString() == "(<= (input ?p noop) (role ?p))"
	}

	@Test
	void testToString_static()
	{
		assert gdl_singleLine.toString() == "(<= (input ?p noop) (role ?p))"
		assert gdl_multiLine.toString() == "(<= (next (control Black))\n(true (control White)))"
	}

}

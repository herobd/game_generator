package game.gdl.statement

import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestGeneratorStatement implements TokenUser
{
	private String player
	private String next_player

	@Before
	void setup()
	{
		player = null
		next_player = null
	}

	@Override
	String getPLAYER()
	{
		if (player == null)
			return GameToken.PLAYER.toString()
		else
			return player
	}

	@Override
	String getNEXT_PLAYER()
	{
		if (next_player == null)
			return GameToken.NEXT_PLAYER.toString()
		else
			return next_player
	}

	@Test
	void test_getPlayer()
	{
		assert this.PLAYER == "PLAYER"
		this.player = "Bob"
		assert this.PLAYER == "Bob"
		this.player = null
		assert this.PLAYER == "PLAYER"
	}

	@Test
	void test_GStringInGenerator()
	{
		GeneratorStatement g = new GeneratorStatement("(<= (next (control ${ -> PLAYER}))\n" +
													  "(true control ${ -> NEXT_PLAYER})))")
		println g.text
		assert g.text.toString() == "(<= (next (control PLAYER))\n(true control NEXT_PLAYER)))"
	}

	@Test
	void test_overrideGString()
	{
		GeneratorStatement g = new GeneratorStatement("(<= (next (control ${ -> PLAYER}))\n" +
				"(true control ${ -> NEXT_PLAYER})))")

		this.player = "Black"
		this.next_player = "White"

		println g.text
		assert g.text.toString() == "(<= (next (control Black))\n(true control White)))"
	}

	@Test
	void test_convertToSimpleStatement()
	{
		GeneratorStatement g = new GeneratorStatement("(<= (next (control ${ -> PLAYER}))\n" +
				"(true control ${ -> NEXT_PLAYER})))")
		this.player = "Red"
		this.next_player = "Blue"
		def simple = new SimpleStatement(g.toString())
		this.player = "Green"
		this.next_player = "Orange"

		assert simple.text == "(<= (next (control Red))\n(true control Blue)))"
		assert g.text.toString() == "(<= (next (control Green))\n(true control Orange)))"
	}
}

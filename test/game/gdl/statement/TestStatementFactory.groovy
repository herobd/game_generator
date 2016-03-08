package game.gdl.statement

import game.GameContextInfo
import game.constructs.player.Players
import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestStatementFactory implements TokenUser
{
	String str1
	String str2
	String str3
	GString gStr1
	Players players
	GameContextInfo contextInfo

	@Before
	void setup()
	{
		str1 = "(<= (next (cell ?m ?n x))\n" +
				"(does White (mark ?m ?n))\n" +
				"(true (cell ?m ?n b)))"

		str2 = "(<= (next (cell ?m ?n o))\n" +
				"(does Black (mark ?m ?n))\n" +
				"(true (cell ?m ?n b)))"

		str3 = "(<= (next (cell ?m ?n b))\n" +
				"(does ?w (mark ?j ?k))\n" +
				"(true (cell ?m ?n b))\n" +
				"(or (distinct ?m ?j) (distinct ?n ?k)))"

		gStr1 = "(<= (next (control ${Tokens.NEXT_PLAYER}))\n" +
				"(true control ${Tokens.PLAYER})))"

		players = new Players(["White", "Black", "Red", "Robot"])
		contextInfo = new GameContextInfo(players)
	}

	@Test
	void test_simples()
	{
		def st1 = new SimpleStatement(str1)
		def st2 = new SimpleStatement(str2)
		def st3 = new SimpleStatement(str3)
		def statements = [st1, st2, st3]
		def result = StatementFactory.interpolateStatements(statements, contextInfo)
		assert result.size() == 3
		assert result[0].toString() == str1
		assert result[1].toString() == str2
		assert result[2].toString() == str3
	}

	@Test
	void test_generated()
	{
		def statements = [new GeneratorStatement(gStr1)]
		def result = StatementFactory.interpolateStatements(statements, contextInfo)
		assert result.size() == 4
		assert result[0].toString() == "(<= (next (control Black))\n(true control White)))"
		assert result[1].toString() == "(<= (next (control Red))\n(true control Black)))"
		assert result[2].toString() == "(<= (next (control Robot))\n(true control Red)))"
		assert result[3].toString() == "(<= (next (control White))\n(true control Robot)))"
	}

	@Test
	void test_mixed()
	{
		def st1 = new SimpleStatement(str1)
		def st2 = new SimpleStatement(str2)
		def st3 = new SimpleStatement(str3)
		def st4 = new GeneratorStatement(gStr1)
		def statements = [st1, st2, st4, st3]
		def result = StatementFactory.interpolateStatements(statements, contextInfo)
		assert result.size() == 7
		assert result[0].toString() == str1
		assert result[1].toString() == str2
		assert result[2].toString() == "(<= (next (control Black))\n(true control White)))"
		assert result[3].toString() == "(<= (next (control Red))\n(true control Black)))"
		assert result[4].toString() == "(<= (next (control Robot))\n(true control Red)))"
		assert result[5].toString() == "(<= (next (control White))\n(true control Robot)))"
		assert result[6].toString() == str3
	}
}

package game.gdl.statement.factory

import game.GameContextInfo
import game.constructs.player.Players
import game.gdl.statement.GenerationStrategy
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.statement.SubstitutionStatement
import game.gdl.statement.TokenUser
import game.gdl.statement.GameToken
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
	GString gStr2
	GString gStr3
	GString gStr4
	GString gStr5
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

		gStr1 = "(<= (next (control ${GameToken.NEXT_PLAYER}))\n" +
				"(true control ${GameToken.PLAYER})))"

		gStr2 = "(<= (legal ${GameToken.PLAYER} noop)\n" +
				"(true (control ${GameToken.OTHER_PLAYER})))"

		gStr3 = "(<= (Draw ${GameToken.PLAYER})\n" +
				"(not (Win ${GameToken.PLAYER}))\n" +
				"${"(not (Win ${GameToken.OTHER_PLAYER}))\n"}" +
				"(not open))"

		gStr4 = "(init (control ${GameToken.FIRST_PLAYER}))"

		gStr5 = "(init (control ${GameToken.LAST_PLAYER}))"

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
	void test_generated_perPlayer()
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
	void test_generated_perOtherPlayer()
	{
		def statements = [new GeneratorStatement(gStr2, GenerationStrategy.PerOtherPlayer)]
		def result = StatementFactory.interpolateStatements(statements, contextInfo)
		assert result.size() == 12
		assert result[0].toString() == "(<= (legal White noop)\n(true (control Black)))"
		assert result[1].toString() == "(<= (legal White noop)\n(true (control Red)))"
		assert result[2].toString() == "(<= (legal White noop)\n(true (control Robot)))"
		assert result[3].toString() == "(<= (legal Black noop)\n(true (control White)))"
		assert result[4].toString() == "(<= (legal Black noop)\n(true (control Red)))"
		assert result[5].toString() == "(<= (legal Black noop)\n(true (control Robot)))"
		assert result[6].toString() == "(<= (legal Red noop)\n(true (control White)))"
		assert result[7].toString() == "(<= (legal Red noop)\n(true (control Black)))"
		assert result[8].toString() == "(<= (legal Red noop)\n(true (control Robot)))"
		assert result[9].toString() == "(<= (legal Robot noop)\n(true (control White)))"
		assert result[10].toString() == "(<= (legal Robot noop)\n(true (control Black)))"
		assert result[11].toString() == "(<= (legal Robot noop)\n(true (control Red)))"
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

	@Test
	void test_inline()
	{
		def statements = [new GeneratorStatement(gStr3)]
		def result = StatementFactory.interpolateStatements(statements, contextInfo)
		assert result[0].toString() ==
				"(<= (Draw White)\n" +
				"(not (Win White))\n" +
				"(not (Win Black))\n" +
				"(not (Win Red))\n" +
				"(not (Win Robot))\n" +
				"(not open))"
	}

	@Test
	void test_substitution()
	{
		def statements = [new SubstitutionStatement(gStr4), new SubstitutionStatement(gStr5)]
		def result = StatementFactory.interpolateStatements(statements, contextInfo)
		assert result[0].toString() == "(init (control White))"
		assert result[1].toString() == "(init (control Robot))"
	}
}

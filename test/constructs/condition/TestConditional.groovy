package constructs.condition

import constructs.condition.functions.GameFunction
import constructs.condition.result.EndGameResult
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestConditional
{
	@Test
	void test_toString()
	{
		Conditional c = new Conditional(GameFunction.N_inARow(3), EndGameResult.Lose)
		assert c.toString() == "3inARow -> Lose"
		println(c.toString())

		def f = new NegatedCondition(GameFunction.N_inARow(4))
		c = new Conditional(f, EndGameResult.Win)
		assert c.toString() == "not 4inARow -> Win"
	}

	@Test
	void test_equals()
	{
		Conditional a = new Conditional(GameFunction.N_inARow(3), EndGameResult.Lose)
		Conditional b = new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Lose)
		Conditional c = new Conditional(new NegatedCondition(GameFunction.N_inARow(4)), EndGameResult.Win)
		Conditional d = new Conditional(new NegatedCondition(GameFunction.N_inARow(4)), EndGameResult.Win)
		assert a == b
		assert a != c
		assert c == d
	}
}

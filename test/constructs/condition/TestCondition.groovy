package constructs.condition

import constructs.condition.functions.Function
import constructs.condition.result.EndGameResult
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestCondition
{
	@Test
	void testToString()
	{
		Condition c = new Condition(Function.N_inARow, EndGameResult.Lose)
		assert c.toString() == "N_inARow -> Lose"
		println(c.toString())
	}
}

package constructs.condition

import constructs.condition.functions.GameFunction
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
		Condition c = new Condition(GameFunction.N_inARow(3), EndGameResult.Lose)
		assert c.toString() == "3inARow -> Lose"
		println(c.toString())
	}
}

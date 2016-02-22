package constructs.condition

import constructs.condition.NegatedCondition
import constructs.condition.functions.GameFunction
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Class for testing complex condtions including NegatedCondition, ConjunctiveCondition, and DisjunctiveCondtion
 */
class TestCondition
{
	@Test
	void test_not__toString()
	{
		// non-parametrized
		def f = GameFunction.Open
		NegatedCondition c = new NegatedCondition(f)
		assert c.toString() == "not Open"

		// parametrized
		f = GameFunction.N_inARow(3)
		c = new NegatedCondition(f)
		assert c.toString() == "not 3inARow"

		// recursive
		c = new NegatedCondition(c)
		assert c.toString() == "not (not 3inARow)"
	}

	@Test
	void test_not__getFunctions()
	{
		def f = GameFunction.Open
		NegatedCondition c = new NegatedCondition(f)
		assert c.functions == [GameFunction.Open]

		//recursive
		c = new NegatedCondition(c)
		assert c.functions == [GameFunction.Open]
	}

}

package game.constructs.condition

import game.constructs.condition.functions.GameFunction
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
		assert c.toString() == "not open"

		// parametrized
		f = GameFunction.N_inARow([3])
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
		c = new NegatedCondition(c)
		c = new NegatedCondition(c)
		c = new NegatedCondition(c)
		assert c.functions == [GameFunction.Open]
	}

	@Test
	void test_hashSet()
	{
		def not_open = new NegatedCondition(GameFunction.Open)
		def not_not_open = new NegatedCondition(not_open)
		def S = new HashSet([GameFunction.Open, GameFunction.N_inARow([3]), not_open, not_not_open, not_open, GameFunction.N_inARow([3])])
		assert S.size() == 4
		assert S.contains(GameFunction.Open)
		assert S.contains(GameFunction.N_inARow([3]))
		assert S.contains(not_open)
		assert S.contains(not_not_open)
	}

}

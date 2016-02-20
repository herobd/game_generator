package constructs.condition.functions

import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestFunction
{
	@Test
	void test3_inARow()
	{
		def f = Function.N_inARow(3)
		assert f == new ParametrizedFunction("3inARow")
		assert !(f instanceof Function)
		assert f instanceof PreCondition
		println f
	}

	@Test
	void testN_inARow__plain()
	{
		def f = Function.N_inARow
		assert f instanceof Function
		println f
		assert f(3) == new ParametrizedFunction("3inARow")
	}

	@Test
	void testOpen()
	{
		def f = Function.Open
		assert f(3) == new ParametrizedFunction("Open")
	}
}

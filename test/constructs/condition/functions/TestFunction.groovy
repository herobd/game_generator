package constructs.condition.functions

import org.junit.Test
import org.junit.Assert

/**
 * @author Lawrence Thatcher
 */
class TestFunction
{
	@Test
	void test3_inARow()
	{
		def f = Function.N_inARow(3)
		assert f == new ParametrizedFunction("3inARow", Function.N_inARow)
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
		assert f(4) == new ParametrizedFunction("4inARow", Function.N_inARow)
	}

	@Test
	void testOpen()
	{
		def f = Function.Open
		assert f instanceof PreCondition
		assert f(5) == f
		assert f(6).toString() == "Open"
	}
}

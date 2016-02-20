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
		assert f == "3inARow"
		assert !(f instanceof Function)
		println f
	}

	@Test
	void testN_inARow__plain()
	{
		def f = Function.N_inARow
		assert f instanceof Function
		println f
		assert f(3) == "3inARow"
	}
}

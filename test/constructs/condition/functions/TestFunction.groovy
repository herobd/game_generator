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
		assert f == new ParametrizedFunction("3inARow", Function.N_inARow)
		assert !(f instanceof Function)
		assert f instanceof GameFunction
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
		assert f instanceof GameFunction
		assert f(5) == f
		assert f(6).toString() == "Open"
	}

	@Test
	void testType()
	{
		assert Function.Open.type == Function.Open
		assert Function.N_inARow.type == Function.N_inARow
		assert Function.N_inARow(3).type == Function.N_inARow
	}

	@Test
	void testNumParams()
	{
		assert Function.Open.numParams == 0
		assert Function.N_inARow.numParams == 1
	}

	@Test
	void testGetFunctionName()
	{
		assert Function.Open.functionName == "open"
		assert Function.N_inARow.functionName == "in_a_row"
		assert Function.N_inARow(3).functionName == "in_a_row"
	}
}

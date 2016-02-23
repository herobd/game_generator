package constructs.condition.functions

import gdl.clauses.base.BaseClause
import gdl.clauses.init.InitClause
import gdl.clauses.legal.LegalClause
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestFunction
{
	@Test
	void test_3inARow()
	{
		def f = GameFunction.N_inARow(3)
		assert f == new ParametrizedFunction("3inARow", GameFunction.N_inARow)
		assert !(f instanceof GameFunction)
		assert f instanceof Function
		println f
	}

	@Test
	void test_NinARow()
	{
		def f = GameFunction.N_inARow
		assert f instanceof GameFunction
		println f
		assert f(4) == new ParametrizedFunction("4inARow", GameFunction.N_inARow)
	}

	@Test
	void test_open()
	{
		def f = GameFunction.Open
		assert f instanceof Function
		assert f(5) == f
		assert f(6).toString() == "open"
	}

	@Test
	void test_type()
	{
		assert GameFunction.Open.type == GameFunction.Open
		assert GameFunction.N_inARow.type == GameFunction.N_inARow
		assert GameFunction.N_inARow(3).type == GameFunction.N_inARow
	}

	@Test
	void test_numParams()
	{
		assert GameFunction.Open.numParams == 0
		assert GameFunction.N_inARow.numParams == 1
	}

	@Test
	void test_getFunctionName()
	{
		assert GameFunction.Open.functionName == "open"
		assert GameFunction.N_inARow.functionName == "in_a_row"
		assert GameFunction.N_inARow(3).functionName == "in_a_row"
	}

	@Test
	void test_retrieveBoardGDL()
	{
		// Non-parametrized
		def mock1 = new MockOpen()
		def f = GameFunction.Open
		def clause = f.retrieveBoardGDL(mock1)
		assert clause instanceof LegalClause

		// Parametrized function - default
		def mock2 = new MockInARow()
		f = GameFunction.N_inARow
		clause = f.retrieveBoardGDL(mock2)
		assert clause instanceof BaseClause
		assert !(clause instanceof InitClause)

		// Parametrized function
		f = GameFunction.N_inARow(4)
		clause = f.retrieveBoardGDL(mock2)
		assert clause instanceof InitClause
	}

	@Test
	void test_getFunctions()
	{
		def f = GameFunction.Open
		assert f.functions == [GameFunction.Open]

		f = GameFunction.N_inARow(4)
		assert f.functions == [GameFunction.N_inARow(4)]
		assert f.functions != [GameFunction.N_inARow(3)]
	}

	@Test
	void test_hashSet()
	{
		def S = new HashSet([GameFunction.Open, GameFunction.N_inARow(3), GameFunction.N_inARow, GameFunction.N_inARow(3)])
		assert S.size() == 3
		assert S.contains(GameFunction.Open)
		assert S.contains(GameFunction.N_inARow)
		assert S.contains(GameFunction.N_inARow(3))
	}
}

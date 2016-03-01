package mock

import constructs.condition.functions.Function
import constructs.condition.functions.GameFunction
import constructs.condition.functions.SupportsInARow
import gdl.clauses.GDLClause
import gdl.clauses.base.BaseClause
import gdl.clauses.init.InitClause

/**
 * @author Lawrence Thatcher
 *
 * A simple class that mocks the SupportsInARow interface
 */
class MockInARow implements SupportsInARow
{
	@Override
	GDLClause in_a_row(int n)
	{
		if (n == 3)
			return new BaseClause([])
		else if (n == 4)
			return new InitClause([])
		else
			return null
	}

	@Override
	boolean supports(Function function)
	{
		return function.type == GameFunction.N_inARow
	}
}

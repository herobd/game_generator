package mock

import game.constructs.condition.functions.Function
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.functions.SupportsInARow
import game.gdl.clauses.GDLClause
import game.gdl.clauses.base.BaseClause
import game.gdl.clauses.init.InitClause

/**
 * @author Lawrence Thatcher
 *
 * A simple class that mocks the SupportsInARow interface
 */
class MockInARow implements SupportsInARow
{
	@Override
	boolean supports(Function function)
	{
		return function.type == GameFunction.N_inARow
	}

	@Override
	GDLClause in_a_row(List n)
	{
		def x = n[0]
		if (x == 3)
			return new BaseClause([])
		else if (x == 4)
			return new InitClause([])
		else
			return null
	}
}

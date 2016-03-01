package mock

import game.constructs.condition.functions.Function
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.functions.SupportsOpen
import game.gdl.clauses.GDLClause
import game.gdl.clauses.legal.LegalClause

/**
 * @author Lawrence Thatcher
 *
 * A simple class that mocks the SupportsOpen interface
 */
class MockOpen implements SupportsOpen
{
	@Override
	GDLClause open()
	{
		return new LegalClause([])
	}

	@Override
	boolean supports(Function f)
	{
		return f == GameFunction.Open
	}
}

package mock

import constructs.condition.functions.Function
import constructs.condition.functions.GameFunction
import constructs.condition.functions.SupportsOpen
import gdl.clauses.GDLClause
import gdl.clauses.legal.LegalClause

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

package constructs.condition.functions

import gdl.clauses.GDLClause
import gdl.clauses.base.BaseClause
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
	List<Function> getSupportedFunctions()
	{
		return [Function.Open]
	}
}

package constructs.condition.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
enum GameFunction implements Function
{
	N_inARow("in_a_row", {n -> n.toString() + "inARow"}),
	Open("open", null);

	private Closure fn
	private String funcName
	private GameFunction(String f, Closure c)
	{
		funcName = f
		this.fn = c
	}

	def call(args)
	{
		if (this.fn == null)
			return this
		ParametrizedFunction f = new ParametrizedFunction(this.fn, this)
		f(args)
		return f
	}

	int getNumParams()
	{
		if (this.fn == null)
			return 0
		return this.fn.maximumNumberOfParameters
	}

	@Override
	String getFunctionName()
	{
		return funcName
	}

	@Override
	GDLClause retrieveBoardGDL(def obj)
	{
		if (this.numParams == 0)
			return (GDLClause){x -> x."$functionName"()}.call(obj)
		else
			return (GDLClause){x -> x."$functionName"(defaultArgument)}.call(obj)
	}

	@Override
	GameFunction getType()
	{
		return this
	}

	private Object getDefaultArgument()
	{
		switch (this)
		{
			case N_inARow:
				return 3
			default:
				return null
		}
	}
}
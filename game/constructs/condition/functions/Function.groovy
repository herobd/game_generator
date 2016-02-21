package constructs.condition.functions

/**
 * @author Lawrence Thatcher
 */
enum Function implements GameFunction
{
	N_inARow("in_a_row", {n -> n.toString() + "inARow"}),
	Open("open", null);

	private Closure fn
	private String funcName
	private Function(String f, Closure c)
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
	Function getType()
	{
		return this
	}
}
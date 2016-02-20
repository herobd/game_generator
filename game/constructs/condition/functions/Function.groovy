package constructs.condition.functions

/**
 * @author Lawrence Thatcher
 */
enum Function implements PreCondition
{
	N_inARow({n -> n.toString() + "inARow"}),
	Open(null)

	private Closure fn
	private Function(Closure c)
	{
		this.fn = c
	}

	def call(args)
	{
		if (this.fn == null)
			return this
		ParametrizedFunction f = new ParametrizedFunction(this.fn)
		f(args)
		return f
	}
}
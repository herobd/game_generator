package constructs.condition.functions

/**
 * @author Lawrence Thatcher
 */
enum Function implements PreCondition
{
	N_inARow({n -> n.toString() + "inARow"}),
	Open({"Open"})

	private def fn
	private Function(Closure c)
	{
		this.fn = c
	}

	def call(args)
	{
		String s = fn(args)
		return new ParametrizedFunction(s)
	}
}
package constructs.condition.functions

/**
 * @author Lawrence Thatcher
 */
class ParametrizedFunction implements PreCondition
{
	private String name = "default"
	private def args
	private def func

	ParametrizedFunction(String name)
	{
		this.name = name
	}

	ParametrizedFunction(Closure c)
	{
		this.func = c
	}

	def call(args)
	{
		this.args = args
		this.name = func(args)
	}

	@Override
	String toString()
	{
		return this.name
	}

	@Override
	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (!(o instanceof ParametrizedFunction))
			return false
		ParametrizedFunction aCase = (ParametrizedFunction) o
		if (name != aCase.name)
			return false
		return true
	}

	@Override
	int hashCode()
	{
		return name.hashCode()
	}
}

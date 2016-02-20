package constructs.condition.functions

/**
 * @author Lawrence Thatcher
 */
class ParametrizedFunction implements PreCondition
{
	private String name

	ParametrizedFunction(String s)
	{
		this.name = s
	}

	@Override
	String toString()
	{
		return this.name
	}

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

	int hashCode()
	{
		return name.hashCode()
	}
}

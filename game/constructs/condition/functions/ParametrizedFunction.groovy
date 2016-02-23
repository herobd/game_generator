package constructs.condition.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
class ParametrizedFunction implements Function
{
	private String name = "default"
	private def args
	private def func
	private GameFunction parent

	ParametrizedFunction(String name, GameFunction parent)
	{
		this.name = name
		this.parent = parent
	}

	ParametrizedFunction(Closure c, GameFunction parent)
	{
		this.func = c
		this.parent = parent
	}

	def call(args)
	{
		this.args = args
		this.name = func(args)
	}

	@Override
	String toString()
	{
		if (name == "default")
			return parent.toString()
		return this.name
	}

	@Override
	GameFunction getType()
	{
		return this.parent
	}

	@Override
	String getFunctionName()
	{
		return this.parent.functionName
	}

	@Override
	GDLClause retrieveBoardGDL(def obj)
	{
		return (GDLClause){x -> x."$functionName"(args)}.call(obj)
	}

	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (!(o instanceof ParametrizedFunction))
			return false

		ParametrizedFunction that = (ParametrizedFunction) o

		if (name != that.name)
			return false
		if (parent != that.parent)
			return false

		return true
	}

	int hashCode()
	{
		int result
		result = name.hashCode()
		result = 31 * result + parent.hashCode()
		return result
	}
}

package constructs.condition.functions

import gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 */
class ParametrizedFunction implements GameFunction
{
	private String name = "default"
	private def args
	private def func
	private Function parent

	ParametrizedFunction(String name, Function parent)
	{
		this.name = name
		this.parent = parent
	}

	ParametrizedFunction(Closure c, Function parent)
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
	Function getType()
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
		return name.hashCode()
	}
}

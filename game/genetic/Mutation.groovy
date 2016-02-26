package genetic

/**
 * @author Lawrence Thatcher
 *
 * A callable container class for representing a possible mutation that could be made.
 */
class Mutation
{
	private Closure closure
	private def parent
	private String name

	Mutation(String methodName, def parent)
	{
		this.parent = parent
		this.name = methodName
		this.closure = {p -> p."$methodName"()}
	}

	def call()
	{
		return closure.call(parent)
	}

	@Override
	String toString()
	{
		return name
	}
}

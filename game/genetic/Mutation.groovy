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

	Mutation(String methodName, def parent)
	{
		this.parent = parent
		this.closure = {p -> p."$methodName"()}
	}

	def call()
	{
		return closure.call(parent)
	}
}

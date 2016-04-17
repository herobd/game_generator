package generator

/**
 * @author Lawrence Thatcher
 *
 * A callable container class for representing a possible mutation that could be made.
 */
class Mutation
{
	protected Closure closure
	protected def parent
	protected String name

	/**
	 * Creates a callable Closure that calls the method in the provided class based on the name
	 * @param methodName the name of the method to call
	 * @param parent the object to call the method in
	 */
	Mutation(String methodName, def parent)
	{
		this.parent = parent
		this.name = methodName
		this.closure = {p -> p."$methodName"()}
	}

	protected Mutation(def parent)
	{
		this.parent = parent
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

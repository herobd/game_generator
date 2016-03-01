package generator

/**
 * @author Lawrence Thatcher
 *
 * Like the Mutation class, but for Cross-Overs.
 */
class CrossOver
{
	private Closure closure

	CrossOver(Closure action)
	{
		this.closure = action
	}

	def call(args)
	{
		return closure.call(args)
	}

	String toString()
	{
		return closure.toString()
	}
}

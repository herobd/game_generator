package genetic

/**
 * @author Lawrence Thatcher
 *
 * Like the Mutation class, but for Cross-Overs.
 */
class GeneCross
{
	private Closure closure

	GeneCross(Closure action)
	{
		this.closure = action
	}

	def call(args)
	{
		return closure.call(args)
	}
}

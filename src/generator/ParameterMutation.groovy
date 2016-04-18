package generator

/**
 * @author Lawrence Thatcher
 *
 * A mutation object meant specifically for parameter updating
 */
class ParameterMutation extends Mutation
{
	private parameterMethod = "changeParam"
	private int amount = 1

	ParameterMutation(Gene parent, int param)
	{
		super(parent)
		if (parent.hasProperty("RANDOM") && parent.RANDOM)
		{
			double a = 0
			while (a.intValue() == 0)
				a = parent.RANDOM.nextGaussian()
			amount = a
		}
		this.name = parameterMethod + "(" + Integer.toString(param) + "," + Integer.toString(amount) + ")"
		this.closure = {p -> p."$parameterMethod"(param, amount)}
	}
}

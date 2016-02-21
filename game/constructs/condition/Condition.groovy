package constructs.condition

import constructs.condition.functions.GameFunction
import constructs.condition.result.Result

/**
 * @author Lawrence Thatcher
 */
class Condition
{
	private GameFunction antecedent
	private Result consequent

	Condition(GameFunction function, Result result)
	{
		this.antecedent = function
		this.consequent = result
	}

	GameFunction getAntecedent()
	{
		return antecedent
	}

	Result getConsequent()
	{
		return consequent
	}

	@Override
	String toString()
	{
		String result = antecedent.toString()
		result += " -> "
		result += consequent.toString()
		return result
	}
}

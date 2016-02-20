package constructs.condition

import constructs.condition.functions.Function
import constructs.condition.functions.PreCondition
import constructs.condition.result.Result

/**
 * @author Lawrence Thatcher
 */
class Condition
{
	private PreCondition antecedent
	private Result consequent

	Condition(PreCondition function, Result result)
	{
		this.antecedent = function
		this.consequent = result
	}

	PreCondition getAntecedent()
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

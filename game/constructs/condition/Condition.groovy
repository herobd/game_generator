package constructs.condition

import constructs.condition.functions.Function
import constructs.condition.result.Result

/**
 * @author Lawrence Thatcher
 */
class Condition
{
	private Function antecedent
	private Result consequent

	Condition(Function function, Result result)
	{
		this.antecedent = function
		this.consequent = result
	}

	Function getAntecedent()
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

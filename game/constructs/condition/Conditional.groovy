package constructs.condition

import constructs.condition.functions.Function
import constructs.condition.result.Result

/**
 * @author Lawrence Thatcher
 *
 * A class for representing game state conditionals, that consist of a condition (antecedent), and a result (consequent)
 */
class Conditional
{
	private Function antecedent
	private Result consequent

	Conditional(Function function, Result result)
	{
		this.antecedent = function
		this.consequent = result
	}

	/**
	 * Retrieves the game state condition that this conditional rests upon
	 * @return a Function type
	 */
	Function getAntecedent()
	{
		return antecedent
	}

	/**
	 * Retrieves the action or result that happens when the game state condition is met
	 * @return a Result type
	 */
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

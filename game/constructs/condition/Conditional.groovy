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
	private Condition antecedent
	private Result consequent

	Conditional(Condition condition, Result result)
	{
		this.antecedent = condition
		this.consequent = result
	}

	/**
	 * Retrieves the game state condition that this conditional rests upon
	 * @return a Function type
	 */
	Condition getAntecedent()
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

	/**
	 * Retrieves a collection of all the Functions that are used in the antecedent
	 * @return a list or set of Functions
	 */
	Collection<Function> getFunctions()
	{
		return antecedent.functions
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

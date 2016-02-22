package constructs.condition

import constructs.condition.functions.Function
import constructs.condition.functions.GameFunction

/**
 * @author Lawrence Thatcher
 *
 * Object representing a simple negated condition.
 */
class NegatedCondition implements Condition
{
	private Condition condition

	/**
	 * Takes in a condition and negates it.
	 * The condition can be either another combined condition or a Function itself.
	 * @param condition A Condition to negate.
	 */
	NegatedCondition(Condition condition)
	{
		this.condition = condition
	}

	@Override
	Collection<Function> getFunctions()
	{
		return condition.functions
	}

	@Override
	String toString()
	{
		String result = "not "
		if (condition instanceof Function)
			result += condition.toString()
		else
			result += "(" + condition.toString() + ")"
		return result
	}
}

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

	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (getClass() != o.class)
			return false

		NegatedCondition that = (NegatedCondition) o
		if (condition != that.condition)
			return false

		return true
	}

	int hashCode()
	{
		return this.toString().hashCode()
	}
}

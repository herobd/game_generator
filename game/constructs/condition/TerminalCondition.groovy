package constructs.condition

import constructs.condition.functions.Function
import constructs.condition.result.EndGameResult
import constructs.condition.result.Result
import constructs.end.EndGameConditions

/**
 * @author Lawrence Thatcher
 *
 * A special case of the Condtional class where the result is restricted to be an EndGameResult
 */
class TerminalCondition extends Conditional
{
	TerminalCondition(Function function, EndGameResult result)
	{
		super(function, result)
	}
}

package game.constructs.condition

import game.constructs.condition.result.EndGameResult


/**
 * @author Lawrence Thatcher
 *
 * A special case of the Condtional class where the result is restricted to be an EndGameResult
 */
class TerminalConditional extends Conditional
{
	TerminalConditional(Condition condition, EndGameResult result)
	{
		super(condition, result)
	}
}

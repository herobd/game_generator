package constructs.board
import constructs.condition.functions.GameFunction

/**
 * @author Lawrence Thatcher
 *
 * Exception to be thrown when you try to call a function that is not supported on the given board
 */
class FunctionNotSupportedException extends Exception
{
	FunctionNotSupportedException(GameFunction func, Exception ex)
	{
		super("The function " + func.toString() + " is not supported on this board", ex)
	}
}

package game.constructs.board
import game.constructs.condition.functions.Function

/**
 * @author Lawrence Thatcher
 *
 * Exception to be thrown when you try to call a function that is not supported on the given board
 */
class FunctionNotSupportedException extends Exception
{
	FunctionNotSupportedException(Function func, Exception ex)
	{
		super("The function " + func.toString() + " is not supported on this board", ex)
	}
}

package game.constructs.board.grid

/**
 * @author Lawrence Thatcher
 *
 * A simple helper class used to represent a variable within coordinates that can be expressed in GDL as a variable.
 * This class implements the ++ and -- operators.
 */
class Index
{
	private static String PREFIX = "?"

	private String var
	private int idx

	Index(String var)
	{
		this.var = var
		this.idx = 0
	}

	Index(String var, int idx)
	{
		this.var = var
		this.idx = idx
	}

	Index next()
	{
		this.idx++
		return this
	}

	Index previous()
	{
		this.idx--
		return this
	}

	String toString()
	{
		String result = PREFIX + var
		if (idx != 0)
			result += Integer.toString(idx)
		return result
	}
}

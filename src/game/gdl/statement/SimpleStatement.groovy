package game.gdl.statement

/**
 * @author Lawrence Thatcher
 *
 * A simple GDL Predicate or Implication
 * (Currently a placeholder class, may be replaced later?)
 */
class SimpleStatement
{
	private String statement
	//TODO: add support for generic/dynamic statements (ex: for player loop, etc...)

	SimpleStatement(String statement)
	{
		this.statement = statement
	}

	public String getText()
	{
		return this.statement
	}

	@Override
	String toString()
	{
		return this.statement
	}

	@Override
	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (getClass() != o.class)
			return false

		SimpleStatement that = (SimpleStatement) o

		if (statement != that.statement)
			return false

		return true
	}

	@Override
	int hashCode()
	{
		return statement.hashCode()
	}
}

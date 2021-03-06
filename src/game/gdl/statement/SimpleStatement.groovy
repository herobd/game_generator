package game.gdl.statement

/**
 * @author Lawrence Thatcher
 *
 * A simple GDL Predicate or Implication
 * (Currently a placeholder class, may be replaced later?)
 */
class SimpleStatement implements GDLStatement
{
	protected String statement

	SimpleStatement(String statement)
	{
		this.statement = statement
	}

	public String getText()
	{
		return this.statement
	}

	@Override
	StatementType getType()
	{
		return StatementType.Simple
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

		GDLStatement that = (GDLStatement) o

		if (this.text != that.text)
			return false

		return true
	}

	@Override
	int hashCode()
	{
		return statement.hashCode()
	}
}

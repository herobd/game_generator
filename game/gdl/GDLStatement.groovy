package gdl

/**
 * @author Lawrence Thatcher
 *
 * A simple GDL Predicate or Implication
 * (Currently a placeholder class, may be replaced later?)
 */
class GDLStatement
{
	private String statement

	GDLStatement(String statement)
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
		return this.text
	}
}

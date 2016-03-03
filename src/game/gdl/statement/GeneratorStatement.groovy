package game.gdl.statement

/**
 * A GDL Statement that generates multiple statements
 * @author Lawrence Thatcher
 */
class GeneratorStatement implements GDLStatement
{
	GString statement

	GeneratorStatement(GString statement)
	{
		this.statement = statement
	}

	@Override
	String getText()
	{
		return this.statement
	}

	String toString()
	{
		return this.statement
	}
}

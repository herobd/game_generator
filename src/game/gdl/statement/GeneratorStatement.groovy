package game.gdl.statement

import org.codehaus.groovy.runtime.GStringImpl

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
	GString getText()
	{
		def values = statement.values.clone()
		return new GStringImpl(values, statement.strings)
	}

	@Override
	boolean isGenerator()
	{
		return true
	}

	@Override
	String toString()
	{
		return this.statement
	}


}

package game.gdl.statement

import org.codehaus.groovy.runtime.GStringImpl

/**
 * @author Lawrence Thatcher
 *
 * Abstract base class for both the GeneratorStatement and SubstitutionStatement classes
 */
abstract class InterpolatedStatement implements GDLStatement
{
	protected GString statement

	InterpolatedStatement(GString statement)
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
	String toString()
	{
		return this.statement
	}
}

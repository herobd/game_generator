package game.gdl.statement

import org.codehaus.groovy.runtime.GStringImpl

/**
 * A GDL Statement that generates multiple statements
 * @author Lawrence Thatcher
 */
class GeneratorStatement implements GDLStatement
{
	private GString statement
	private GenerationStrategy strategy

	GeneratorStatement(GString statement)
	{
		this.statement = statement
		this.strategy = GenerationStrategy.PerPlayer
	}

	GeneratorStatement(GString statement, GenerationStrategy strategy)
	{
		this.statement = statement
		this.strategy = strategy
	}

	GenerationStrategy getStrategy()
	{
		return this.strategy
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

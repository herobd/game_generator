package game.gdl.statement

import org.codehaus.groovy.runtime.GStringImpl

/**
 * A GDL Statement that generates multiple statements
 * @author Lawrence Thatcher
 */
class GeneratorStatement extends InterpolatedStatement implements GDLStatement
{
	private GenerationStrategy strategy

	GeneratorStatement(GString statement)
	{
		super(statement)
		this.strategy = GenerationStrategy.PerPlayer
	}

	GeneratorStatement(GString statement, GenerationStrategy strategy)
	{
		super(statement)
		this.strategy = strategy
	}

	GenerationStrategy getStrategy()
	{
		return this.strategy
	}

	@Override
	StatementType getType()
	{
		return StatementType.Generator
	}
}

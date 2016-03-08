package game.gdl.statement.factory

import game.GameContextInfo
import game.gdl.statement.GDLStatement
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.factory.GeneratorFactory

/**
 * @author Lawrence Thatcher
 *
 * The statement factory holds methods and functionality for generating basic GDL-Statements out of
 * generators or substitutors.
 */
class StatementFactory
{
	static List<GDLStatement> interpolateStatements(List<GDLStatement> statements, GameContextInfo contextInfo)
	{
		def result = []
		for (GDLStatement statement : statements)
		{
			if (!statement.generator)
			{
				result.add(statement)
			}
			else
			{
				def generator = new GeneratorFactory(contextInfo)
				def simples = generator.generateForPlayer(statement as GeneratorStatement)
				result.addAll(simples)
			}
		}
		return result
	}
}

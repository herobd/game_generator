package game.gdl.statement.factory

import game.GameContextInfo
import game.gdl.statement.GDLStatement
import game.gdl.statement.GenerationStrategy
import game.gdl.statement.GeneratorStatement
import static game.gdl.statement.StatementType.*

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
			if (statement.type == Simple)
			{
				result.add(statement)
			}
			else if (statement.type == Generator)
			{
				GeneratorStatement gs = statement as GeneratorStatement
				def generator = new GeneratorFactory(contextInfo)
				def simples = []
				if (gs.strategy == GenerationStrategy.PerPlayer)
					simples = generator.generateForPlayer(gs)
				else if (gs.strategy == GenerationStrategy.PerOtherPlayer)
					simples = generator.generateForOtherPlayers(gs)
				result.addAll(simples)
			}
			else if (statement.type == Substitution)
			{

			}
		}
		return result
	}
}

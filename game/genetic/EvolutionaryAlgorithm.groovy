package genetic

import constructs.TurnOrder
import constructs.board.grid.SquareGrid
import constructs.condition.NegatedCondition
import constructs.condition.TerminalConditional
import constructs.condition.functions.GameFunction
import constructs.condition.result.EndGameResult
import constructs.player.Players
import game.Game

/**
 * A class that does an evolutionary algorithm.
 *
 * @author Lawrence Thatcher
 */
class EvolutionaryAlgorithm
{
	private List<Evolvable> population = []
	private static final Random RANDOM = new Random()

	EvolutionaryAlgorithm(List<Evolvable> initialPop)
	{
		population = initialPop
	}

	public static void main(String[] args)
	{
		//For now using a hard-coded initial population...
		def players = new Players(["White", "Black", "Salmon", "Pink"])
		def board = new SquareGrid(3, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Game p1 = new Game(players, board, TurnOrder.Alternating, [], end)

		players = new Players(["Red", "Orange", "Green", "Yellow", "Gold"])
		Game p2 = new Game(players, board, TurnOrder.Alternating, [], end)

		EvolutionaryAlgorithm algorithm = new EvolutionaryAlgorithm([p1, p2])

		int iters = 50
		if (args.length > 0)
			iters = new Integer(args[0])
		println "Doing " + Integer.toString(iters) + " iterations."

		algorithm.run(iters)

		algorithm.printPopulationMembers()
	}

	//TODO: shouldn't have to specify number of iterations...
	def run(int iterations)
	{
		for (int i = 0; i < iterations; i++)
		{
			//Parent Selection
			def p1 = randomMember
			def p2 = randomMember
			while (p2 == p1)
				p2 = randomMember

			//Cross-Over and Mutation
			def p3 = p1.crossOver(p2)
			p3.mutate()

			//Evaluation
			// TODO: cull inbreds
			// TODO: add controller hook here

			//Add to population
			population.add(p3)
		}
	}

	//Helper Methods
	void printPopulationMembers()
	{
		for (Evolvable member : population)
		{
			println member.toString()
			println "\n"
		}
	}

	private Evolvable getRandomMember()
	{
		//TODO: Use stochastic universal sampling to select fitter individuals more often
		int idx = RANDOM.nextInt(population.size())
		return population[idx]
	}
}

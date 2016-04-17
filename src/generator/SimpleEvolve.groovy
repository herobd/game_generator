package generator

import game.Game
import game.constructs.TurnOrder
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.NegatedCondition
import game.constructs.condition.TerminalConditional
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.result.EndGameResult
import game.constructs.player.Players

/**
 * @author Lawrence Thatcher
 */
class SimpleEvolve
{
	protected static final Random RANDOM = new Random()
	def population = []

	SimpleEvolve()
	{
		//For now using a hard-coded initial population...
		def players = new Players(["White", "Black", "Salmon", "Pink"])
		def board = new SquareGrid(3, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow([3]), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Game p1 = new Game(players, board, TurnOrder.Alternating, [], end)

		players = new Players(["Red", "Orange", "Green", "Yellow", "Gold"])
		def board2 = new SquareGrid(5, false)
		Game p2 = new Game(players, board2, TurnOrder.Alternating, [], end)

		population.add(p1)
		population.add(p2)
	}

	public static void main(String[] args)
	{
		SimpleEvolve s = new SimpleEvolve()
		s.run(20)
		for (def p : s.population)
		{
			println p.toString()
		}
	}

	void run(int iterations)
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

			population.add(p3)
		}
	}

	Game getRandomMember()
	{
		return population[RANDOM.nextInt(population.size())] as Game
	}
}

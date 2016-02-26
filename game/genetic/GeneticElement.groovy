package genetic

import java.util.concurrent.Callable

/**
 * @author Lawrence Thatcher
 *
 * Interface that game constructs implement in order to facilitate the genetic evolutionary process.
 */
trait GeneticElement
{
	private final static double DEFAULT_MUTATION_PROBABILITY = 0.1
	private final static Random RANDOM = new Random()

	abstract List<Mutation> getPossibleMutations()

	def mutate(double probability)
	{
		def mutations = this.possibleMutations
		for (Mutation m : mutations)
		{
			double d = RANDOM.nextDouble()
			if (d <= probability)
			{
				m.call()
			}
		}
	}

	def mutate()
	{
		mutate(DEFAULT_MUTATION_PROBABILITY)
	}

	Random getRANDOM()
	{
		return RANDOM
	}
}
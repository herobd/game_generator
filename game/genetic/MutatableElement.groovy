package genetic

import java.util.concurrent.Callable

/**
 * @author Lawrence Thatcher
 *
 * Interface that game constructs implement in order to facilitate the genetic evolutionary process.
 */
trait MutatableElement
{
	private final static double DEFAULT_MUTATION_PROBABILITY = 0.1
	private final static Random RANDOM = new Random()

	/**
	 * Should return a list of callable Mutation objects,
	 * representing the possible mutations available in the implementing class.
	 * @return a list of Mutation objects
	 */
	abstract List<Mutation> getPossibleMutations()

	/**
	 * Performs each of the possible mutations,
	 * each with the specified probability
	 * @param probability the probability for each possible mutation to occur independently
	 */
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

	/**
	 * Performs each possible mutation, each with a probability of 10%
	 */
	def mutate()
	{
		mutate(DEFAULT_MUTATION_PROBABILITY)
	}

	/**
	 * The Random object getter.
	 * Useful so the the implementing class doesn't have to create another instance of Random.
	 * @return the Random object
	 */
	Random getRANDOM()
	{
		return RANDOM
	}

	/**
	 * Simple helper method that returns a new Mutation object.
	 * The Mutation object overloads the call operator,
	 * which calls the method in the implementing class that has the specified name.
	 * @param name the name of the method to call when this mutation occurs
	 * @return a Mutation object for calling the specified method
	 */
	Mutation mutationMethod(String name)
	{
		return new Mutation(name, this)
	}
}
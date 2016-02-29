package genetic

/**
 * @author Lawrence Thatcher
 *
 * Interface that game constructs implement in order to facilitate the genetic evolutionary process.
 */
trait Gene
{
	private final static double DEFAULT_MUTATION_PROBABILITY = 0.1
	private final static double DEFAULT_CROSS_OVER_PROBABILITY = 0.1
	private final static Random RANDOM = new Random()

	/**
	 * Should return a list of callable Mutation objects,
	 * representing the possible mutations available in the implementing class.
	 * @return a list of Mutation objects
	 */
	abstract List<Mutation> getPossibleMutations()

	/**
	 * Should return a list of callable GeneCross objects,
	 * representing the possible cross-overs available with the other object.
	 * @param other the other
	 * @return a list of GeneCross objects
	 */
	abstract List<GeneCross> getPossibleCrossOvers(Gene other)

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

	/**
	 * Returns whether or not the specified element is compatible to be crossed over with
	 * @param other the other element to check if it is compatible
	 * @return true if they are of the same type, false otherwise
	 */
	boolean compatible(Gene other)
	{
		return (other.class == this.class)
	}
}
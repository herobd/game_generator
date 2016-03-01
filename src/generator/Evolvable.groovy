package generator

/**
 * @author Lawrence Thatcher
 *
 * Interface indicating that the implementing object is a type that supports the basic generator operators and can
 * be used in an evolutionary algorithm.
 */
interface Evolvable
{
	/**
	 * Crosses-over this object with the other specified Evolvable object, producing a new offspring.
	 * @param mate the other Evolvable object to cross-over with
	 * @return a new Evolvable object of the same type, that resulted from the cross-over.
	 */
	Evolvable crossOver(Evolvable mate)

	/**
	 * Performs standard Gene mutation on this object, with a probability of 10% for each possible mutation
	 */
	def mutate()

	/**
	 * Creates a deep copy of itself.
	 * This method should probably be expressly implemented in the implementing class,
	 * to ensure that the copy is in fact a deep copy.
	 */
	Evolvable clone()

	/**
	 * Retrieves a list of potential genes from this object that can be crossed-over with and mutated
	 * @return a list of all the elements of this class that implement the Gene interface
	 */
	List<Gene> getGenes()
}
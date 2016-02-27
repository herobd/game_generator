package genetic

/**
 * @author Lawrence Thatcher
 *
 * Interface indicating that the implementing object is a type that supports the basic genetic operators and can
 * be used in an evolutionary algorithm.
 */
interface Evolvable
{
	Evolvable crossOver(Evolvable mate)

	def mutate()

	/**
	 * Creates a deep copy of itself.
	 * This method should probably be expressly implemented in the implementing class,
	 * to ensure that the copy is in fact a deep copy.
	 */
	Evolvable clone()
}
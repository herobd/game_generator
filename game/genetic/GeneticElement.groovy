package genetic

import java.util.concurrent.Callable

/**
 * @author Lawrence Thatcher
 *
 * Interface that game constructs implement in order to facilitate the genetic evolutionary process.
 */
interface GeneticElement
{
	List<Mutation> getPossibleMutations()


}
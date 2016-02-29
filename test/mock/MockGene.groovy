package mock

import genetic.GeneCross
import genetic.Gene
import genetic.Mutation

/**
 * @author Lawrence Thatcher
 *
 * A simple mock Gene object.
 */
class MockGene implements Gene
{

	@Override
	List<Mutation> getPossibleMutations()
	{
		return []
	}

	@Override
	List<GeneCross> getPossibleCrossOvers(Gene other)
	{
		return []
	}
}

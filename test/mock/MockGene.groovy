package mock

import genetic.GeneCross
import genetic.MutatableElement
import genetic.Mutation

/**
 * @author Lawrence Thatcher
 *
 * A simple mock Gene object.
 */
class MockGene implements MutatableElement
{

	@Override
	List<Mutation> getPossibleMutations()
	{
		return []
	}

	@Override
	List<GeneCross> getPossibleCrossOvers(MutatableElement other)
	{
		return []
	}
}

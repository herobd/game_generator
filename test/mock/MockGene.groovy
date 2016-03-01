package mock

import generator.CrossOver
import generator.Gene
import generator.Mutation

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
	List<CrossOver> getPossibleCrossOvers(Gene other)
	{
		return []
	}
}

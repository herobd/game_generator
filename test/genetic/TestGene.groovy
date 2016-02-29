package genetic

import constructs.player.Players
import mock.MockGene
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Includes tests for the MutatableElement trait
 */
class TestGene
{
	@Test
	void test_compatible()
	{
		MutatableElement players = new Players(2)
		MutatableElement mock = new MockGene()
		assert !players.compatible(mock)
	}
}

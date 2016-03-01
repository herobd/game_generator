package genetic

import constructs.player.Players
import mock.MockGene
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Includes tests for the Gene trait
 */
class TestGene
{
	@Test
	void test_compatible()
	{
		Gene players = new Players(2)
		Gene mock = new MockGene()
		assert !players.compatible(mock)
	}
}

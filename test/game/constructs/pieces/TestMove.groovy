package game.constructs.pieces

import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestMove
{
	@Test
	void test_rand_num_sections()
	{
		Move m = new Move()
		Map<Integer, Integer> counts = [:]
		for (int i = 0; i < 100; i++)
		{
			int j = m.rand_num_sections()

			assert j > 0

			if (!counts.keySet().contains(j))
				counts[j] = 1
			else
				counts[j] += 1
		}
		println counts
	}
}

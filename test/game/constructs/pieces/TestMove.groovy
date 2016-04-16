package game.constructs.pieces

import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestMove
{
	@Test
	void test_rand_positive()
	{
		Move m = new Move()
		Map<Integer, Integer> counts = [:]
		for (int i = 0; i < 100; i++)
		{
			int j = m.rand_positive()

			assert j > 0

			if (!counts.keySet().contains(j))
				counts[j] = 1
			else
				counts[j] += 1
		}
		println counts
	}

	@Test
	void test_constructor__empty()
	{
		Move m
		for (int i = 0; i < 20; i++)
		{
			m = new Move()
			println m.toString()
		}
	}
}

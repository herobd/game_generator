package game.constructs.player

import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Tests for the PlayerName enum
 */
class TestPlayerName
{
	@Test
	void test_getRandom()
	{
		for(int i = 0; i < 10; i++)
		{
			PlayerName p = PlayerName.random
			println p.toString()
			assert PlayerName.values().contains(p)
		}
	}

	@Test
	void test_contains()
	{
		assert PlayerName.contains("White")
		assert PlayerName.contains("Ghost")
		assert !PlayerName.contains("John-Jacob-Jingleheimer-Smith")
	}

	@Test
	void test_toPlayerName()
	{
		assert PlayerName.toPlayerName("White") == PlayerName.White
		assert PlayerName.toPlayerName("His-Name-Is-My-Name-Too") == PlayerName.Unknown
	}
}

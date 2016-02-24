package constructs.player

import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Tests for the Players class
 */
class TestPlayers
{
	@Test
	void test_addNewPlayer()
	{
		Players players = new Players(["White", "Black"])
		assert players.toString() == "White Black "
		players.addNewPlayer()
		assert players.toString() == "White Black Red "
	}

	@Test
	void test_inNames()
	{
		Players players = new Players(["White", "Black", "Red", "Robot"])
		assert players.inNames(PlayerName.White)
		assert players.inNames(PlayerName.Black)
		assert players.inNames(PlayerName.Red)
		assert players.inNames(PlayerName.Robot)
		assert !players.inNames(PlayerName.Blue)
	}

	@Test
	void test_getPossibleMutations()
	{
		Players players = new Players(["White", "Black"])
		def M = players.possibleMutations
		assert M.size() == 1
		assert players.size() == 2

		M[0].call(players)
		print players.toString()
		assert players.size() == 3
		def names = players.playerNames
		assert PlayerName.contains(names[2])
		assert names[2] == "Red"
	}
}

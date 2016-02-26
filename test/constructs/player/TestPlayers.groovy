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
		// normal ordering, goes to next
		Players players = new Players(["White", "Black"])
		assert players.toString() == "White Black "
		players.addNewPlayer()
		assert players.toString() == "White Black Red "

		// keeps iterating until it finds an unused vale
		players = new Players(["Black", "Red", "White"])
		assert players.toString() == "Black Red White "
		players.addNewPlayer()
		assert players.toString() == "Black Red White Blue "

		// uses default if last name is not recognized
		players = new Players(["Black", "Red", "Bob_the_Builder"])
		players.addNewPlayer()
		assert players.toString() == "Black Red Bob_the_Builder Unknown "
		players.addNewPlayer()
		assert players.toString() == "Black Red Bob_the_Builder Unknown White "
	}

	@Test
	void test_removePlayer()
	{
		Players players
		for (int i = 0; i < 5; i++)
		{
			players = new Players(["White", "Black", "Red"])
			players.removePlayer()
			println players.toString()
			assert players.toString() == "White Black " || "White Red " || "Black Red "
		}
	}

	@Test
	void test_changeName()
	{
		Players players = new Players(["White", "Black", "Red"])
		String output = "White Black Red "
		for (int i = 0; i < 15; i++)
		{
			players.changeName()
			println players.toString()
			assert output != players.toString()
		}
	}

	@Test
	void test_canRemoveAPlayer()
	{
		Players players = new Players(["White", "Black", "Red"])
		assert players.canRemoveAPlayer()
		players = new Players(["White", "Black"])
		assert !players.canRemoveAPlayer()
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
		assert !players.inNames(null)
	}

	@Test
	void test_getPossibleMutations()
	{
		// cannot remove a player if there are only two players already
		Players players = new Players(["White", "Black"])
		def mutations = players.possibleMutations
		assert mutations.size() == 1
		assert players.size() == 2

		// add a player
		mutations[0].call()
		assert players.size() == 3
		def names = players.playerNames
		assert PlayerName.contains(names[2])  // new player name comes from known list
		assert names[2] == "Red"

		// remove a player does appear with 3 players or more
		mutations = players.possibleMutations
		assert mutations.size() == 2
		assert players.size() == 3

		// remove a player
		mutations[0].call()
		assert players.size() == 2
		assert players.toString() == "White Black " || "White Red " || "Black Red "
	}
}

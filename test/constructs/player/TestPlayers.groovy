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
		assert mutations.size() == 2
		assert players.size() == 2

		// add a player
		mutations[0].call()
		assert players.size() == 3
		def names = players.playerNames
		assert PlayerName.contains(names[2])  // new player name comes from known list
		assert names[2] == "Red"

		// remove a player does appear with 3 players or more
		mutations = players.possibleMutations
		assert mutations.size() == 3
		assert players.size() == 3

		// remove a player
		mutations[0].call()
		assert players.size() == 2
		assert players.toString() == "White Black " || "White Red " || "Black Red "
		String prev = players.toString()

		// change name
		mutations[2]()
		println players.toString()
		assert players.toString() != prev
	}

	@Test
	void test_getPossibleCrossOvers()
	{
		Players p1 = new Players(["White", "Black"])
		Players p2 = new Players(["Red", "Blue", "Green"])
		Players p3 = new Players(["Purple", "Pink"])
		Players p4 = new Players(["Orange", "Yelllow", "Gold"])

		//crossing with a larger element
		def crossOvers = p1.getPossibleCrossOvers(p2)
		assert crossOvers.size() == 3

		//swap
		crossOvers[0].call(p2)
		assert p1 == new Players(["Red", "Black"])
		crossOvers[1].call(p3)
		assert p1 == new Players(["Red", "Pink"])

		//add
		crossOvers[2].call(p2)
		assert p1 == new Players(["Red", "Pink", "Green"])

		//crossing with a smaller element
		crossOvers = p4.getPossibleCrossOvers(p3)
		assert crossOvers.size() == 2
	}

	@Test
	void test_mutate()
	{
		Players players = new Players(["White", "Black"])
		String next = players.toString()
		for(int i = 0; i < 10; i++)
		{
			players.mutate(1)
			println players.toString()
			assert next != players.toString()
			next = players.toString()
		}
	}

	@Test
	void test_mutate__default()
	{
		Players players = new Players(["White", "Black"])
		String next = players.toString()
		int count = 0
		for(int i = 0; i < 1000; i++)
		{
			players.mutate()
			if (players.toString() != next)
				count++
			next = players.toString()
		}
		double mut_prob = (double)count / 1000.0
		println "mutation probability: " + Double.toString(mut_prob)
		assert count > 0
	}

	@Test
	void test_clone()
	{
		Players p1 = new Players(["White", "Black", "Red", "Robot"])
		Players p2 = p1.clone()
		assert !p1.is(p2)
		assert  p1 == p2
	}

	@Test
	void test_indexing()
	{
		Players players = new Players(["White", "Black", "Red", "Robot"])
		// getAt
		assert players[0].toString() == "White"
		assert players[-1].toString() == "Robot"

		// putAt
		players[0] = new Player("Cool")
		assert players.size() == 4
		assert players.toString() == "Cool Black Red Robot "
		players[-1] = new Player("Camel")
		assert players.size() == 4
		assert players.toString() == "Cool Black Red Camel "
		players[4] = new Player("Goose")
		assert players.size() == 5
		assert players.toString() == "Cool Black Red Camel Goose "
	}

	@Test
	void test_plus()
	{
		Players players = new Players(["White", "Black"])
		Player p = new Player("Blue")
		players += p
		assert players.size() == 3
		assert players.toString() == "White Black Blue "

		p = new Player("Green")
		assert (players + p).toString() == "White Black Blue Green "
		assert players.size() == 3
		assert players.toString() == "White Black Blue "
	}
}

package game

import game.constructs.player.Players
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Tests for the GameContextInfo class
 */
class TestGameContextInfo
{
	@Test
	void test_getPlayers()
	{
		Players players = new Players(["White", "Black", "Red"])
		def context = new GameContextInfo(players)
		def p = context.players
		assert p[0].PLAYER == "White"
		assert p[0].PLAYER_MARK == "white"
		assert p[0].NEXT_PLAYER == "Black"

		assert p[1].PLAYER == "Black"
		assert p[1].PLAYER_MARK == "black"
		assert p[1].NEXT_PLAYER == "Red"

		assert p[2].PLAYER == "Red"
		assert p[2].PLAYER_MARK == "red"
		assert p[2].NEXT_PLAYER == "White"
	}
}

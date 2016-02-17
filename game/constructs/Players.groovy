package constructs

import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 *
 * Stores information concerning the Players construct, including number of players and the player names.
 */
class Players implements HasClauses
{
	private List<String> players = []

	Players(int numPlayers)
	{
		for (int i = 0; i < numPlayers; i++)
			this.players.add("Player" + Integer.toString(i+1))
	}

	Players(Iterable<String> players)
	{
		for (String p : players)
			this.players.add(p)
	}

	/**
	 * The String names of the players for this game
	 * @return a list of the players' names
	 */
	def getPlayerNames()
	{
		return this.players
	}

	/**
	 * Whether or not this game uses the RANDOM player.
	 * This searches for the word 'random' appearing as part of any player name (case insensitive)
	 *
	 * @return true if so, false otherwise
	 */
	boolean hasRandomPlayer()
	{
		for (String p : this.players)
		{
			if (p.toLowerCase().contains('random'))
				return true
		}
		return false
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return []
	}
}

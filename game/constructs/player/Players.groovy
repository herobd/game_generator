package constructs.player

import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.role.HasRolesClause
import gdl.clauses.role.RolesClause
import genetic.GeneticElement

/**
 * @author Lawrence Thatcher
 *
 * Stores information concerning the Players construct,
 * including number of players and the players' names.
 */
class Players implements HasClauses, HasRolesClause, GeneticElement
{
	private List<Player> players = []

	Players(int numPlayers)
	{
		for (int i = 0; i < numPlayers; i++)
			this.players.add(new Player("Player" + Integer.toString(i+1)))
	}

	Players(Iterable<String> players)
	{
		for (String p : players)
			this.players.add(new Player(p))
	}

	/**
	 * The String names of the players for this game
	 * @return a list of the players' names
	 */
	List<String> getPlayerNames()
	{
		def result = []
		for (Player p : players)
			result.add(p.name)
		return result
	}

	/**
	 * Whether or not this game uses the RANDOM player.
	 * This searches for the word 'random' appearing as part of any player name (case insensitive)
	 *
	 * @return true if so, false otherwise
	 */
	boolean hasRandomPlayer()
	{
		for (Player p : this.players)
		{
			if (p.name.toLowerCase().contains('random'))
				return true
		}
		return false
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		return [this.rolesClause]
	}

	@Override
	RolesClause getRolesClause()
	{
		//TODO: Replace with GDL-statement generator
		List<GDLStatement> playerDefs = []
		for (String name : this.playerNames)
		{
			def statement = new GDLStatement("(role " + name + ")")
			playerDefs.add(statement)
		}
		return new RolesClause(playerDefs)
	}

	@Override
	List<Closure> getPossibleMutations()
	{
		return [{n -> n.addNewPlayer()}]
	}

	def addNewPlayer()
	{
		Player p = players[-1]
		PlayerName name = p.toPlayerName()
		while(inNames(name))
		{
			name = name.next()
		}
		players.add(name.toPlayer())
	}

	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		return super.clone()
	}

	boolean inNames(PlayerName name)
	{
		return playerNames.contains(name.toString())
	}

	@Override
	String toString()
	{
		String result = ""
		for (Player p : players)
		{
			result += p.name + " "
		}
		return result
	}

	int size()
	{
		return players.size()
	}

}

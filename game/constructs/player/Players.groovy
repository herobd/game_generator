package constructs.player

import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.role.HasRolesClause
import gdl.clauses.role.RolesClause
import genetic.MutatableElement

/**
 * @author Lawrence Thatcher
 *
 * Stores information concerning the Players construct,
 * including number of players and the players' names.
 */
class Players implements HasClauses, HasRolesClause, MutatableElement
{
	private List<Player> players = []

	/**
	 * Creates a new list of players with the specified number of players.
	 * The names of the players will be of the form: Player1, Player2, etc...
	 * @param numPlayers the number of players to generate
	 */
	Players(int numPlayers)
	{
		for (int i = 0; i < numPlayers; i++)
			this.players.add(new Player("Player" + Integer.toString(i+1)))
	}

	/**
	 * Creates a list of players based off of the specified names
	 * @param players
	 */
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

	// Implements Methods
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

	/**
	 * Gets the possible mutations available on this Players object.
	 * The possible mutations are:
	 * 	addNewPlayer()	--adds a new player
	 * 	removePlayer()	--removes a player (not available if result would be less than 2 players)
	 * 	changeName()	--changes a player's name
	 *
	 * @return a list of closures that can be called to execute the given function.
	 * 		   Each closure takes in a reference to this Players object as a parameter.
	 */
	@Override
	List<Closure> getPossibleMutations()
	{
		def result = []
		if (canRemoveAPlayer())
			result.add(mutationMethod("removePlayer"))
		result.add(mutationMethod("addNewPlayer"))
		result.add(mutationMethod("changeName"))
		return result
	}

	// Genetic Functions
	/**
	 * Adds a new player to this player set, using the next available PlayerName in the series.
	 */
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

	/**
	 * Removes a randomly selected player from this player set.
	 * In general this method should not be called to reduce the number of players down to below two.
	 */
	def removePlayer()
	{
		int idx = RANDOM.nextInt(this.size())
		this.players.removeAt(idx)
	}

	/**
	 * Selects a player at random and randomly changes their name to one not already in use
	 */
	def changeName()
	{
		PlayerName name = PlayerName.random
		while (inNames(name))
		{
			name = PlayerName.random
		}
		int idx = RANDOM.nextInt(this.size())
		this.players[idx].updateTo(name.toPlayer())
	}

	// Helper Methods
	/**
	 * Checks to see if a given name is already in use.
	 * @param name the name to check against
	 * @return true if already in use, false otherwise
	 */
	protected boolean inNames(PlayerName name)
	{
		return playerNames.contains(name.toString())
	}

	/**
	 * Only allows a player to be removed if there are already at least 2 players
	 * @return true if size > 2, false otherwise
	 */
	protected boolean canRemoveAPlayer()
	{
		return this.size() > 2
	}

	// List-like Functions
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

	Player getAt(int idx)
	{
		return players[idx]
	}
}

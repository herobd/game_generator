package constructs.player

/**
 * @author Lawrence Thatcher
 *
 * A simple class used to represent a player in the game.
 */
class Player
{
	private String name
	private String mark_token = null

	Player(String name)
	{
		this.name = name
	}

	Player(String name, mark_token)
	{
		this.name = name
		this.mark_token = mark_token
	}

	//Getters
	String getName()
	{
		return this.name
	}

	String getMarkToken()
	{
		if (mark_token == null)
			return name.toLowerCase()
		return mark_token
	}

	// Public Methods
	/**
	 * Gets the PlayerName that matches this Player's name.
	 * If none match, it returns the default PlayerName (Unknown)
	 * @return a PlayerName
	 */
	PlayerName toPlayerName()
	{
		PlayerName result = PlayerName.toPlayerName(this.name)
		return result
	}

	/**
	 * Updates the current player's name and mark_token to the ones given
	 * @param player the Player object to take the name and mark_token from
	 */
	void updateTo(Player player)
	{
		this.name = player.name
		this.mark_token = player.mark_token
	}

	//Helper Functions
	@Override
	String toString()
	{
		return this.name
	}

	@Override
	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (!(o instanceof Player))
			return false

		Player player = (Player) o

		if (name != player.name)
			return false

		return true
	}

	@Override
	int hashCode()
	{
		int result
		result = name.hashCode()
		result = 31 * result + mark_token.hashCode()
		return result
	}
}

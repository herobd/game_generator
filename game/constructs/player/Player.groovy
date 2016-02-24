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

	String toString()
	{
		return this.name
	}
}

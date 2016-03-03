package game.gdl.statement

/**
 * @author Lawrence Thatcher
 *
 * An interface that specifes this object may create a generator or substitutor statement,
 * and provides default implementations of token-value getters so the origin class can create
 * the statement using GStrings.
 */
trait TokenUser
{
	String getPLAYER()
	{
		return Tokens.PLAYER.toString()
	}

	String getPLAYER_MARK()
	{
		return Tokens.PLAYER_MARK.toString()
	}

	String getNEXT_PLAYER()
	{
		return Tokens.NEXT_PLAYER.toString()
	}
}
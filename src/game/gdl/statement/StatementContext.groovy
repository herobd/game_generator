package game.gdl.statement

/**
 * @author Lawrence Thatcher
 *
 * Contains getters for tokens used by the substitution and generator statements.
 */
class StatementContext
{
	private String player
	private String player_mark
	private String next_player

	StatementContext(String player, String player_mark, String next_player)
	{
		this.player = player
		this.player_mark = player_mark
		this.next_player = next_player
	}

	String getPLAYER()
	{
		return player
	}

	String getPLAYER_MARK()
	{
		return player_mark
	}

	String getNEXT_PLAYER()
	{
		return next_player
	}
}
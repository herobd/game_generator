package game

import game.constructs.player.Player
import game.constructs.player.Players
import game.gdl.statement.StatementContext

/**
 * @author Lawrence Thatcher
 *
 * This class is used for storing information from seperate game components in a common spot which is necessary for
 * using Generator and Substitution statements.
 */
class GameContextInfo
{
	private Players _players

	GameContextInfo(Players players)
	{
		this._players = players
	}

	List<StatementContext> getPlayers()
	{
		def result = []
		for (Player p : _players)
		{
			Player next = _players.next(p)
			result.add(new StatementContext(p.name, p.markToken, next.name))
		}
		return result
	}
}

package game.gdl.statement.factory

import game.GameContextInfo
import game.gdl.statement.GDLStatement
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.statement.TokenUser
import game.gdl.statement.GameToken
import org.codehaus.groovy.runtime.GStringImpl


/**
 * @author Lawrence Thatcher
 *
 * Class used for generating generator-statement statements.
 * An individual class was needed to override the TokenUser interface to use the correct values,
 * rather than the default values.
 */
class GeneratorFactory implements TokenUser
{
	private GameContextInfo contextInfo
	private int idx

	/**
	 * @param contextInfo requires GameContextInfo to retrieve info about players, etc...
	 */
	GeneratorFactory(GameContextInfo contextInfo)
	{
		this.contextInfo = contextInfo
		idx = 0
	}

	/**
	 * Generates a list of SimpleStatements, one for each player in the game
	 * @param statement the GeneratorStatement where each token will be substituted for the appropriate player value
	 * @return a list of SimpleStatements where each token has been replaced with the appropriate player value
	 */
	List<GDLStatement> generateForPlayer(GeneratorStatement statement)
	{
		def result = []
		for (idx = 0; idx < contextInfo.players.size(); idx++)
		{
			GString gString = statement.text
			rewriteString(gString)

			def s = new SimpleStatement(gString)
			result.add(s)
		}
		idx = 0
		return result
	}

	List<GDLStatement> generateForOtherPlayers(GeneratorStatement statement)
	{
		def result = []
		for (idx = 0; idx < contextInfo.players.size(); idx++)
		{
			for (int j in otherPlayers)
			{
				GString gString = statement.text
				rewriteString(gString, j)
				def s = new SimpleStatement(gString)
				result.add(s)
			}
		}
		idx = 0
		return result
	}

	private void rewriteString(GString gString)
	{
		rewriteString(gString, -1)
	}

	private void rewriteString(GString gString, int otherPlayerID)
	{
		def vals = gString.values
		for (int i = 0; i < vals.length; i++)
		{
			if (vals[i] instanceof GameToken)
			{
				vals[i] = replaceToken(vals[i] as GameToken, otherPlayerID)
			}
			else if (vals[i] instanceof GString && otherPlayerID == -1)
			{
				GString line = vals[i] as GString
				if (line.values.contains(GameToken.OTHER_PLAYER) || line.values.contains(GameToken.OTHER_PLAYER_MARK))
				{
					GString result = GString.EMPTY
					for (int j in otherPlayers)
					{
						GString line_j = new GStringImpl(line.values.clone(), line.strings)
						rewriteString(line_j, j)
						result += line_j
					}
					vals[i] = result
				}
			}
		}
	}

	private def replaceToken(GameToken t, int otherPlayerID)
	{
		switch (t)
		{
			case GameToken.PLAYER:
				return this.PLAYER
			case GameToken.PLAYER_MARK:
				return this.PLAYER_MARK
			case GameToken.NEXT_PLAYER:
				return this.NEXT_PLAYER
			case GameToken.OTHER_PLAYER:
				if (otherPlayerID != -1)
					return contextInfo.players[otherPlayerID].PLAYER
				break
			case GameToken.OTHER_PLAYER_MARK:
				if (otherPlayerID != -1)
					return contextInfo.players[otherPlayerID].PLAYER_MARK
				break
			default:
				return t
		}
		return t
	}

	List<Integer> getOtherPlayers()
	{
		def result = []
		for (int i = 0; i < contextInfo.players.size(); i++)
		{
			if (i != idx)
				result.add(i)
		}
		return result
	}

	@Override
	String getPLAYER()
	{
		return contextInfo.players[idx].PLAYER
	}

	@Override
	String getPLAYER_MARK()
	{
		return contextInfo.players[idx].PLAYER_MARK
	}

	@Override
	String getNEXT_PLAYER()
	{
		return contextInfo.players[idx].NEXT_PLAYER
	}
}

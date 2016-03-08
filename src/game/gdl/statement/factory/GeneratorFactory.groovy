package game.gdl.statement.factory

import game.GameContextInfo
import game.gdl.statement.GDLStatement
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.statement.TokenUser
import game.gdl.statement.Tokens


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

	private void rewriteString(GString gString)
	{
		def vals = gString.values
		for (int i = 0; i < vals.length; i++)
		{
			if (vals[i] instanceof Tokens)
			{
				vals[i] = replaceToken(vals[i] as Tokens)
			}
		}
	}

	private String replaceToken(Tokens t)
	{
		switch (t)
		{
			case Tokens.PLAYER:
				return this.PLAYER
			case Tokens.PLAYER_MARK:
				return this.PLAYER_MARK
			case Tokens.NEXT_PLAYER:
				return this.NEXT_PLAYER
			default:
				return t.toString()
		}
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

package game.gdl.statement.factory

import game.GameContextInfo
import game.gdl.statement.GDLStatement
import game.gdl.statement.GameToken
import game.gdl.statement.SimpleStatement
import game.gdl.statement.SubstitutionStatement

/**
 * @author Lawrence Thatcher
 *
 * Class used for filling-in substitution values in a substitution statement.
 *
 */
class SubstitutionFactory
{
	private GameContextInfo contextInfo

	SubstitutionFactory(GameContextInfo contextInfo)
	{
		this.contextInfo = contextInfo
	}

	GDLStatement substitute(SubstitutionStatement statement)
	{
		GString gString = statement.text
		def vals = gString.values
		for (int i = 0; i < vals.length; i++)
		{
			if (vals[i] instanceof GameToken)
			{
				vals[i] = replaceToken(vals[i] as GameToken)
			}
		}
		return new SimpleStatement(gString)
	}

	private def replaceToken(GameToken t)
	{
		switch (t)
		{
			case GameToken.FIRST_PLAYER:
				return this.contextInfo.players[0].PLAYER
			case GameToken.LAST_PLAYER:
				return this.contextInfo.players[-1].PLAYER
			default:
				return t
		}
	}
}

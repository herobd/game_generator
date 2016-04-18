package game.constructs.pieces.query

import game.constructs.board.Board
import game.constructs.condition.functions.Function
import game.gdl.clauses.GDLClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.statement.GameToken
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 */
class AnyEnemies implements Query
{
	@Override
	GString toGDL(Board board, String piece_id, int n)
	{
		return "(true (anyEnemies ${GameToken.PLAYER}))"
	}

	@Override
	void setGlobalRules(Map<String, GDLClause> globalRules, Board board)
	{
		if (!globalRules.containsKey("anyEnemies"))
		{
			globalRules['anyEnemies'] = new DynamicComponentsClause([enemiesGDL(board)])
		}
	}

	static def enemiesGDL(Board board)
	{
		return new GeneratorStatement("(<= (anyEnemies ${GameToken.PLAYER})\n" +
									  "(true (cell ${board.getSelectedSpaceGDL(0).join(' ')} ?p))\n" +
									  "(not (${GameToken.PLAYER_MARK} ?p)))")
	}

	@Override
	int complexityCount()
	{
		return 1
	}

	@Override
	String convertToJSON()
	{
		return '{"query":"AnyEnemies"}'
	}

	@Override
	Collection<Function> getFunctions()
	{
		return []
	}

	@Override
	def getGDL_Signature()
	{
		return "anyEnemies ${GameToken.PLAYER}" as GString
	}

	@Override
	int getNumParams()
	{
		return 0
	}

	@Override
	void changeParam(int param, int amount)
	{

	}
}

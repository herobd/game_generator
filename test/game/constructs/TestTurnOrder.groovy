package game.constructs

import game.GameContextInfo
import game.constructs.player.Players
import game.gdl.clauses.ClauseType
import game.gdl.statement.factory.StatementFactory
import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestTurnOrder
{
	Players players
	GameContextInfo contextInfo

	@Before
	void setup()
	{
		players = new Players(["Red", "Black", "Robot"])
		contextInfo = new GameContextInfo(players)
	}

	@Test
	void testAlternating_LegalClause()
	{
		def clause = TurnOrder.Alternating.legalClause
		assert clause.clauseType == ClauseType.Legal
		def statements = clause.statements
		assert statements[0].text.toString() == "(<= (legal PLAYER noop)\n(not (true (control PLAYER))))"
	}

	@Test
	void testAlternating_DynamicClause()
	{
		def clause = TurnOrder.Alternating.dynamicComponentClause
		assert clause.clauseType == ClauseType.DynamicComponents
		def statements = clause.statements
		assert statements[0].text.toString() == "(<= (next (control NEXT_PLAYER))\n(true (control PLAYER)))"
	}

	@Test
	void testAlternating_BaseClause()
	{
		def clause = TurnOrder.Alternating.baseAndInputClause
		assert clause.clauseType == ClauseType.BaseAndInput
		def statements = clause.statements
		assert statements[0].text =="(<= (base (control ?p)) (role ?p))"
		assert statements[1].text == "(<= (input ?p noop) (role ?p))"
	}

	@Test
	void testAlternating_InitClause()
	{
		def clause = TurnOrder.Alternating.initialStateClause
		assert clause.clauseType == ClauseType.InitialState
		def statements = clause.statements
		statements = StatementFactory.interpolateStatements(statements, contextInfo)
		assert statements[0].text == "(init (control Red))"
	}
}

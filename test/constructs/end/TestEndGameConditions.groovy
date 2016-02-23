package constructs.end

import constructs.board.grid.SquareGrid
import constructs.condition.Condition
import constructs.condition.Conditional
import constructs.condition.NegatedCondition
import constructs.condition.TerminalConditional
import constructs.condition.functions.GameFunction
import constructs.condition.result.EndGameResult
import gdl.clauses.dynamic.DynamicComponentsClause
import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * A class for testing the EndGameConditions class
 */
class TestEndGameConditions
{
	Condition not_open
	def E
	def board
	EndGameConditions end

	@Before
	void setup()
	{
		not_open = new NegatedCondition(GameFunction.Open)
		E = [new TerminalConditional(not_open, EndGameResult.Draw),
				 new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win)]
		board = new SquareGrid(3)
		end = new EndGameConditions(E, board)
	}

	@Test
	void test_getSupportedBoardGDLClauses()
	{
		def clauses = end.supportedBoardGDLClauses
		assert clauses.size() == 2
		assert clauses[0] instanceof DynamicComponentsClause
		assert clauses[1] instanceof DynamicComponentsClause
	}

	@Test
	void test_getSupportedConditionals()
	{
		def conditionals = end.supportedConditionals
		for (def c : conditionals)
			assert c instanceof Conditional
		assert conditionals.contains(new TerminalConditional(not_open, EndGameResult.Draw))
		assert conditionals.contains(new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win))
		assert conditionals.size() == 2
	}
}

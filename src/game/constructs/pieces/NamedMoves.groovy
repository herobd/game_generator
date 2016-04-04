package game.constructs.pieces

import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.statement.GameToken

/**
 * @author Lawrence Thatcher
 *
 * A reference class that contains pre-defined moves for a piece.
 */
final class NamedMoves
{
	public static Move getMark()
	{
		def inputs = ["index x", "index y"]
		def dc1 = new GeneratorStatement(
				"(<= (next (cell ?m ?n ${GameToken.PLAYER_MARK}))\n" +
				"(does ${GameToken.PLAYER} (mark ?m ?n))\n" +
				"(true (cell ?m ?n b)))")
		def dc3 = new SimpleStatement(
				"(<= (next (cell ?m ?n b))\n" +
				"(does ?w (mark ?j ?k))\n" +
				"(true (cell ?m ?n b))\n" +
				"(or (distinct ?m ?j) (distinct ?n ?k)))")
		def dynComps = [dc1, dc3]
		def i = new SimpleStatement("(<= (input ?p (mark ?x ?y)) (index ?x) (index ?y) (role ?p))")
		def l = new SimpleStatement(
				"(<= (legal ?w (mark ?x ?y))\n" +
				"(true (cell ?x ?y b))\n" +
				"(true (control ?w)))")

		return new Move(inputs, dynComps, i, l)
	}
	
	public static Move getMark()
	{
		Condition precondition = GameFunction.SelectedOpen()
        List<Action> postconditions = [GameActions.MoveToSelected()]
		return new Move(precondition,postconditions)
	}
	
	
}

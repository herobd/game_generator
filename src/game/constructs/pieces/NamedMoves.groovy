package game.constructs.pieces

import game.gdl.statement.GDLStatement

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
		def dc1 = new GDLStatement(
				"(<= (next (cell ?m ?n x))\n" +
				"(does White (mark ?m ?n))\n" +
				"(true (cell ?m ?n b)))")
		def dc2 = new GDLStatement(
				"(<= (next (cell ?m ?n o))\n" +
				"(does Black (mark ?m ?n))\n" +
				"(true (cell ?m ?n b)))")
		def dc3 = new GDLStatement(
				"(<= (next (cell ?m ?n b))\n" +
				"(does ?w (mark ?j ?k))\n" +
				"(true (cell ?m ?n b))\n" +
				"(or (distinct ?m ?j) (distinct ?n ?k)))")
		def dynComps = [dc1, dc2, dc3]
		def i = new GDLStatement("(<= (input ?p (mark ?x ?y)) (index ?x) (index ?y) (role ?p))")
		def l = new GDLStatement(
				"(<= (legal ?w (mark ?x ?y))\n" +
				"(true (cell ?x ?y b))\n" +
				"(true (control ?w)))")

		return new Move(inputs, dynComps, i, l)
	}
}

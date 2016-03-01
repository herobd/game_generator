package game.constructs.board.grid

import game.constructs.board.Board
import game.gdl.GDLStatement
import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.base.BaseClause

/**
 * @author Lawrence Thatcher
 *
 * Generic Grid that has cells
 */
abstract class Grid extends Board implements HasClauses
{
	protected static GDLClause getHasCellsClause()
	{
		def b = new GDLStatement("(<= (base (cell ?x ?y b)) (index ?x) (index ?y))")
		def x = new GDLStatement("(<= (base (cell ?x ?y x)) (index ?x) (index ?y))")
		def o = new GDLStatement("(<= (base (cell ?x ?y o)) (index ?x) (index ?y))")

		return new BaseClause([b,x,o])
	}
}

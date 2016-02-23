package constructs.board.grid

import constructs.board.Board
import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.base.BaseClause

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

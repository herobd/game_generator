package game.constructs.board.grid

import game.constructs.board.Board
import game.gdl.statement.SimpleStatement
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
		def b = new SimpleStatement("(<= (base (cell ?x ?y b)) (index ?x) (index ?y))")
		def x = new SimpleStatement("(<= (base (cell ?x ?y x)) (index ?x) (index ?y))")
		def o = new SimpleStatement("(<= (base (cell ?x ?y o)) (index ?x) (index ?y))")

		return new BaseClause([b,x,o])
	}
}

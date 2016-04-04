package game.constructs.board.grid

import game.constructs.board.Board
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.base.BaseClause
import game.gdl.statement.GameToken

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
		def x = new GeneratorStatement("(<= (base (cell ?x ?y ${GameToken.PLAYER_MARK})) (index ?x) (index ?y))")

		return new BaseClause([b,x])
	}
	
	GDLClause MoveToSelected(List n)
	{
		//define all next for this move
		"(<= (next (cell ?mTo ?nTo piece_name) (cell ?mFrom ?nFrom b) (cell ?m ?n ?var)) \
        (does player_name (move_name ?mTo ?nTo ?mFrom ?nFrom)) \
        (true (cell ?m ?n ?var)) \
        (distinct ?m ?mTo) \
        (distinct ?m ?mFrom) \
        (distinct ?n ?nTo) \
        (distinct ?n ?nFrom) \
        )"
        
	}
}

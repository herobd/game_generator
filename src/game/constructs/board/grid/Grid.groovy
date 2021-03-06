package game.constructs.board.grid

import game.constructs.board.Board
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.SimpleStatement
import game.gdl.clauses.GDLClause
//import game.gdl.clauses.HasClausesWithDep
import game.gdl.clauses.base.BaseClause
import game.gdl.statement.GameToken

/**
 * @author Lawrence Thatcher
 *
 * Generic Grid that has cells
 */
abstract class Grid extends Board //implements HasClausesWithDep
{
	protected static GDLClause getHasCellsClause()
	{
		def b = new SimpleStatement("(<= (base (cell ?x ?y b)) (index ?x) (index ?y))")
		def x = new GeneratorStatement("(<= (base (cell ?x ?y ?piece)) (index ?x) (index ?y) (${GameToken.PLAYER_MARK} ?piece))")

		return new BaseClause([b,x])
	}
	
	
	@Override
	String getGeneralSpaceGDL()
	{
	    return "(cell ?gridm ?gridn ?gridpiece)"
	}
	
	@Override
	String getGeneralSpaceGDLIndex(int i)
	{
	    return ["?gridm", "?gridn"][i]
	}
	
	
	@Override
	List<String> getSelectedSpaceGDL(int i)
	{
	    return ["?selm${i}", "?seln${i}"]
	}
}

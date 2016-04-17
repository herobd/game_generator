package game.constructs.pieces.query

import game.constructs.board.Board
import game.constructs.board.grid.Increment
import game.constructs.board.grid.Index
import game.constructs.board.grid.SquareGrid
import game.gdl.clauses.GDLClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 */
class InARow implements Query
{
	private int num = 3
	private boolean nbors=true
	private boolean i_nbors=false
	private boolean any_piece=false

	@Override
	GString toGDL(Board board, String piece_id, int n)
	{
		return null
	}

	@Override
	void setGlobalRules(Map<String, GDLClause> globalRules, Board board)
	{
		if (board instanceof SquareGrid)
		{

		}
		else
		{
			throw new GroovyRuntimeException("InARow not implemented for board type")
		}
	}

	@Override
	int complexityCount()
	{
		return 0
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

	String getName()
	{
		return Integer.toString(num) + "inARow"
	}

	GDLClause in_a_row(int l)
	{
		String name = Integer.toString(l) + "inARow"

		def s = []
		if (this.nbors)
		{
			s.add(line_row(l))
			s.add(line_column(l))
		}
		if (this.i_nbors)
		{
			s.add(line_diag_desc(l))
			s.add(line_diag_asc(l))
		}
		if (this.nbors)
		{
			s.add(new SimpleStatement("(<= (" + name + " ?w) (row${l} ?x ?y ?w))"))
			s.add(new SimpleStatement("(<= (" + name + " ?w) (column${l} ?x ?y ?w))"))
		}
		if (this.i_nbors)
			s.add(new SimpleStatement("(<= (" + name + " ?w) (diagonal${l} ?x ?y ?w))"))

		return new DynamicComponentsClause(s)
	}

	private static SimpleStatement line_row(int l)
	{
		return line_gen("row", l, Increment.Ascending, Increment.None)
	}

	private static SimpleStatement line_column(int l)
	{
		return line_gen("column",l, Increment.None, Increment.Ascending)
	}

	private static SimpleStatement line_diag_desc(int l)
	{
		return line_gen("diagonal", l, Increment.Ascending, Increment.Ascending)
	}

	private static SimpleStatement line_diag_asc(int l)
	{
		return line_gen("diagonal", l, Increment.Ascending, Increment.Descending)
	}

	private static SimpleStatement line_gen(String name, int l, Increment xInc, Increment yInc)
	{
		String result = ""
		result += "(<= (${name + Integer.toString(l)} ?x ?y ?w)\n"
		def x = new Index("x")
		if (xInc == Increment.Descending)
			x = new Index("x", l-1)
		def y = new Index("y")
		if (yInc == Increment.Descending)
			y = new Index("y", l-1)
		String xSucc = ""
		String ySucc = ""
		String s = ""
		for (int i = 1; i <= l; i++)
		{
			if (x.value > 0)
			{
				xSucc += "(succ ${x-1} ${x})\n"
			}
			if (y.value > 0)
			{
				ySucc += "(succ ${y-1} ${y})\n"
			}
			s += "(true (cell  ${x} ${y} ?w))"
			if (i < l)
				s += "\n"
			xInc(x)
			yInc(y)
		}
		result += xSucc
		result += ySucc
		result += s
		result += ")\n"
		return new SimpleStatement(result)
	}
}

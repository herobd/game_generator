package game.constructs.board.grid

import game.constructs.condition.functions.SupportsInARow
import game.constructs.condition.functions.SupportsOpen
import game.gdl.clauses.GDLClause
import game.gdl.clauses.HasClauses
import game.gdl.clauses.base.BaseClause
import game.gdl.clauses.base.HasBaseClause
import game.gdl.clauses.dynamic.DynamicComponentsClause
import game.gdl.clauses.init.HasInitClause
import game.gdl.clauses.init.InitClause
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 *
 * A square grid with square tiles.
 * TODO: support different x,y sizes
 */
class SquareGrid extends Grid implements
		HasClauses,
		HasBaseClause,
		HasInitClause,
		SupportsInARow,
		SupportsOpen
{
	private int size
	private boolean i_nbors = false

	public SquareGrid(int size)
	{
		this.size = size
	}

	public SquareGrid(int size, boolean i_nbors)
	{
		this.size = size
		this.i_nbors = i_nbors
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		def clauses = []
		clauses.add(hasCellsClause)
		clauses.add(generateIndexClause())
		clauses.add(generateSuccessorClause())
		clauses.add(generateInitClause())
		return clauses
	}

	@Override
	BaseClause getBaseAndInputClause()
	{
		def result = (BaseClause)generateIndexClause()
		result.join(generateSuccessorClause())
		return result
	}

	@Override
	InitClause getInitialStateClause()
	{
		return (InitClause)generateInitClause()
	}

	@Override
	GDLClause in_a_row(List n)
	{
		String name = Integer.toString(n[0]) + "inARow"

		def s = []
		s.add(line_row(n[0]))
		s.add(line_column(n[0]))
		if (this.i_nbors)
		{
			s.add(line_diag_desc(n[0]))
			s.add(line_diag_asc(n[0]))
		}
		s.add(new SimpleStatement("(<= (" + name + " ?w) (row${n[0]} ?x ?y ?w))"))
		s.add(new SimpleStatement("(<= (" + name + " ?w) (column${n[0]} ?x ?y ?w))"))
		if (this.i_nbors)
			s.add(new SimpleStatement("(<= (" + name + " ?w) (diagonal${n[0]} ?x ?y ?w))"))

		return new DynamicComponentsClause(s)
	}

	@Override
	GDLClause open()
	{
		SimpleStatement s = new SimpleStatement("(<= open\n(true (cell ?x ?y b)))")
		return new DynamicComponentsClause([s])
	}

	@Override
	SquareGrid clone()
	{
		return new SquareGrid(this.size, this.i_nbors)
	}

	protected GDLClause generateIndexClause()
	{
		def indices = []
		for (int i = 1; i <= this.size; i++)
		{
			SimpleStatement s = new SimpleStatement("(index " + Integer.toString(i) + ")")
			indices.add(s)
		}
		return new BaseClause(indices)
	}

	protected GDLClause generateSuccessorClause()
	{
		def succs = []
		for (int i = 1; i < this.size; i++)
		{
			SimpleStatement s = new SimpleStatement("(succ " + Integer.toString(i) + " " + Integer.toString(i+1) +  ")")
			succs.add(s)
		}
		return new BaseClause(succs)
	}

	protected GDLClause generateInitClause()
	{
		def cells = []
		for (int i = 1; i <= this.size; i++)
		{
			for (int j = 1; j <= this.size; j++)
			{
				// TODO: the last value should be taken from some abstract representation of the board start state
				SimpleStatement s = new SimpleStatement("(init (cell " + Integer.toString(i) + " " + Integer.toString(j) + " b))")
				cells.add(s)
			}
		}
		return new InitClause(cells)
	}

	private static SimpleStatement line_row(int n)
	{
		return line_gen("row", n, Increment.Ascending, Increment.None)
	}

	private static SimpleStatement line_column(int n)
	{
		return line_gen("column",n, Increment.None, Increment.Ascending)
	}

	private static SimpleStatement line_diag_desc(int n)
	{
		return line_gen("diagonal", n, Increment.Ascending, Increment.Ascending)
	}

	private static SimpleStatement line_diag_asc(int n)
	{
		return line_gen("diagonal", n, Increment.Ascending, Increment.Descending)
	}

	private static SimpleStatement line_gen(String name, int n, Increment xInc, Increment yInc)
	{
		String result = ""
		result += "(<= (${name + Integer.toString(n)} ?x ?y ?w)\n"
		def x = new Index("x")
		if (xInc == Increment.Descending)
			x = new Index("x", n-1)
		def y = new Index("y")
		if (yInc == Increment.Descending)
			y = new Index("y", n-1)
		String xSucc = ""
		String ySucc = ""
		String s = ""
		for (int i = 1; i <= n; i++)
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
			if (i < n)
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
	
	@Override
	int getNumParams()
	{
	    return 1
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        size+=amount
        /*if (param==0)
            size+=amount
        else
            i_nbors=!i_nbors*/
    }
}

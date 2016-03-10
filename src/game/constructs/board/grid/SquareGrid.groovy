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
	GDLClause in_a_row(int n)
	{
		String name = Integer.toString(n) + "inARow"

		def s = []
		s.add(line_gen(n))
		s.add(line_column(n))
		if (this.i_nbors)
		{
			s.add(line_diag_desc(n))
			s.add(line_diag_asc(n))
		}
		s.add(new SimpleStatement("(<= (" + name + " ?w) (row ?x ?y ?w))"))
		s.add(new SimpleStatement("(<= (" + name + " ?w) (column ?x ?y ?w))"))
		if (this.i_nbors)
			s.add(new SimpleStatement("(<= (" + name + " ?w) (diagonal ?x ?y ?w))"))

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

	//TODO: remove code duplication in these somehow?
	private static SimpleStatement line_row(int n)
	{
		String result = ""
		result += "(<= (row ?x ?y ?w)\n"
		for (int i = 1; i <= n; i++)
		{
			result += "(true (cell " + addSuccessors("?x", i) + " ?y ?w))"
			if (i < n)
				result += "\n"
		}
		result += ")"
		return new SimpleStatement(result)
	}

	private static SimpleStatement line_column(int n)
	{
		String result = ""
		result += "(<= (column ?x ?y ?w)\n"
		for (int i = 1; i <= n; i++)
		{
			result += "(true (cell ?x " + addSuccessors("?y", i) + " ?w))"
			if (i < n)
				result += "\n"
		}
		result += ")"
		return new SimpleStatement(result)
	}

	private static SimpleStatement line_diag_desc(int n)
	{
		String result = ""
		result += "(<= (diagonal ?x ?y ?w)\n"
		for (int i = 1; i <= n; i++)
		{
			result += "(true (cell " + addSuccessors("?x", i) + " " + addSuccessors("?y", i) + " ?w))"
			if (i < n)
				result += "\n"
		}
		result += ")"
		return new SimpleStatement(result)
	}

	private static SimpleStatement line_diag_asc(int n)
	{
		String result = ""
		result += "(<= (diagonal ?x ?y ?w)\n"
		for (int i = 1; i <= n; i++)
		{
			result += "(true (cell " + addSuccessors("?x", i) + " " + addSuccessors("?y", n-i+1) + " ?w))"
			if (i < n)
				result += "\n"
		}
		result += ")"
		return new SimpleStatement(result)
	}

	private static SimpleStatement line_gen(int n)
	{
		String result = ""
		result += "(<= (row ?x ?y ?w)\n"
		def x = new Index("x")
		def y = new Index("y")
		String s = ""
		for (int i = 1; i <= n; i++)
		{
			if (x.value > 0)
			{
				result += "(succ ${x-1} ${x})\n"
			}
			s += "(true (cell  ${x} ?y ?w))"
			if (i < n)
				s += "\n"
			x++
		}
		result += s
		result += ")"
		return new SimpleStatement(result)
	}

	private static String addSuccessors(String var, int i)
	{
		String result = var
		for (int j = 0; j < i-1; j++)
		{
			result = "succ(" + result + ")"
		}
		return result
	}
}

package constructs.board.grid

import constructs.condition.functions.GameFunction
import constructs.condition.functions.SupportsInARow
import constructs.condition.functions.SupportsOpen
import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.base.BaseClause
import gdl.clauses.base.HasBaseClause
import gdl.clauses.dynamic.DynamicComponentsClause
import gdl.clauses.init.HasInitClause
import gdl.clauses.init.InitClause

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
		s.add(line_row(n))
		s.add(line_column(n))
		if (this.i_nbors)
		{
			s.add(line_diag_desc(n))
			s.add(line_diag_asc(n))
		}
		s.add(new GDLStatement("(<= (" + name + " ?w) (row ?x ?y ?w))"))
		s.add(new GDLStatement("(<= (" + name + " ?w) (column ?x ?y ?w))"))
		if (this.i_nbors)
			s.add(new GDLStatement("(<= (" + name + " ?w) (diagonal ?x ?y ?w))"))

		return new DynamicComponentsClause(s)
	}

	@Override
	GDLClause open()
	{
		GDLStatement s = new GDLStatement("(<= open\n(true (cell ?x ?y b)))")
		return new DynamicComponentsClause([s])
	}

	protected GDLClause generateIndexClause()
	{
		//TODO: use statement generator
		def indices = []
		for (int i = 1; i <= this.size; i++)
		{
			GDLStatement s = new GDLStatement("(index " + Integer.toString(i) + ")")
			indices.add(s)
		}
		return new BaseClause(indices)
	}

	protected GDLClause generateSuccessorClause()
	{
		//TODO: use statement generator
		def succs = []
		for (int i = 1; i < this.size; i++)
		{
			GDLStatement s = new GDLStatement("(succ " + Integer.toString(i) + " " + Integer.toString(i+1) +  ")")
			succs.add(s)
		}
		return new BaseClause(succs)
	}

	protected GDLClause generateInitClause()
	{
		//TODO: use statement generator
		def cells = []
		for (int i = 1; i <= this.size; i++)
		{
			for (int j = 1; j <= this.size; j++)
			{
				// TODO: the last value should be taken from some abstract representation of the board start state
				GDLStatement s = new GDLStatement("(init (cell " + Integer.toString(i) + " " + Integer.toString(j) + " b))")
				cells.add(s)
			}
		}
		return new InitClause(cells)
	}

	//TODO: remove code duplication in these somehow?
	private static GDLStatement line_row(int n)
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
		return new GDLStatement(result)
	}

	private static GDLStatement line_column(int n)
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
		return new GDLStatement(result)
	}

	private static GDLStatement line_diag_desc(int n)
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
		return new GDLStatement(result)
	}

	private static GDLStatement line_diag_asc(int n)
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
		return new GDLStatement(result)
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

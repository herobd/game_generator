package constructs.board.grid

import gdl.GDLStatement
import gdl.clauses.GDLClause
import gdl.clauses.HasClauses
import gdl.clauses.base.BaseClause
import gdl.clauses.base.HasBaseClause
import gdl.clauses.init.HasInitClause
import gdl.clauses.init.InitClause

/**
 * @author Lawrence Thatcher
 *
 * A square grid with square tiles.
 * TODO: support different x,y sizes
 */
class SquareGrid extends Grid implements HasClauses, HasBaseClause, HasInitClause
{
	private int size

	public SquareGrid(int size)
	{
		this.size = size
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

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		def clauses = []
		clauses.add(hasCellsClause)
		clauses.add(generateIndexClause())
		clauses.add(generateInitClause())
		return clauses
	}

	@Override
	BaseClause getBaseAndInputClause()
	{
		return (BaseClause)generateIndexClause()
	}

	@Override
	InitClause getInitialStateClause()
	{
		return (InitClause)generateInitClause()
	}
}

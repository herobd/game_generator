package constructs.pieces

import gdl.clauses.GDLClause
import gdl.clauses.HasClauses

/**
 * @author Lawrence Thatcher
 *
 * An abstract representation of a game piece.
 */
class Piece implements HasClauses
{
	private String name = ""	//perhaps change to type later..?
	private Placement placement
	private List<Move> moves

	Piece(String name, Placement placement, List<Move> moves)
	{
		this.name = name
		this.placement = placement
		this.moves = moves
	}

	Piece(Placement placement, List<Move> moves)
	{
		this.placement = placement
		this.moves = moves
	}

	@Override
	Collection<GDLClause> getGDLClauses()
	{
		def clauses = []
		clauses += placement.GDLClauses
		for (Move m : moves)
			clauses += m.GDLClauses
		return clauses
	}
}

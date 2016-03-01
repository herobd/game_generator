package game.constructs.pieces

/**
 * @author Lawrence Thatcher
 *
 * A reference class that contains pre-defined pieces.
 */
final class NamedPieces
{
	public static Piece getDEFAULT_PIECE()
	{
		return new Piece(Placement.Persistent, [NamedMoves.mark])
	}
}

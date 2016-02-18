package constructs.pieces

/**
 * @author Lawrence Thatcher
 *
 * A reference class that contains pre-defined pieces.
 */
final class LegalPieces
{
	public static Piece getDEFAULT_PIECE()
	{
		return new Piece(Placement.Persistent, [LegalMoves.mark])
	}
}

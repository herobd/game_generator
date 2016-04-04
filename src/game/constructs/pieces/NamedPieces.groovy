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
	
	public static Piece getPawn()
	{
		Condition precondition1 = Conditional.And(GameFunction.SelectedOpen,GameFunction.SelectedNext())
        List<Action> postconditions1 = [GameAction.MoveToSelected]
		Move basic new Move(precondition1,postconditions1)
		
		Condition precondition2 = Conditional.And(GameFunction.SelectedHas(ENEMY,ANY),Conditional.Or(GameFunction.SelectedRelative(1,1),GameFunction.SelectedRelative(-1,1)))
        List<Action> postconditions2 = [GameAction.CaptureSelected,GameAction.MoveToSelected]
		Move capture new Move(precondition,postconditions)
		
		return new Piece([basic,capture]);
	}
}

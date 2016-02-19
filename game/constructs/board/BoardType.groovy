package constructs.board

/**
 * @author Lawrence Thatcher
 *
 * An enum for representing different types of game boards
 *
 * Grid: A simple grid (such as chess or tic-tac-toe)
 */
enum BoardType
{
	Grid(null),
		Square(Grid)


	private BoardType parent
	private BoardType(BoardType parent)
	{
		this.parent = parent
	}

	BoardType getParent()
	{
		return this.parent
	}
}

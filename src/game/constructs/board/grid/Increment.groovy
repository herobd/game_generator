package game.constructs.board.grid

/**
 * @author Lawrence Thatcher
 *
 * A small helper class used in conjunction with the Index class, for defining how to increment a variable.
 */
enum Increment
{
	Ascending,
	Descending,
	None

	Index call(Index var)
	{
		switch (this)
		{
			case Ascending:
				return ++var
			case Descending:
				return --var
			case None:
			default:
				return var
		}
	}
}

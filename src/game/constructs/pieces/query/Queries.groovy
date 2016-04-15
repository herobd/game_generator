package game.constructs.pieces.query

/**
 * @author Lawrence Thatcher
 *
 * Enum class that is a list of known queries, and getters for creating the appropriate Query object
 */
enum Queries
{
	IsEnemy,
	IsNeighbor,
	IsOpen

	private static final Random RANDOM = new Random()
	private static final List<Queries> VALUES = Collections.unmodifiableList(Arrays.asList(values())) as List<Queries>
	private static final int SIZE = values().size()

	Query getQuery()
	{
		switch (this)
		{
			case IsEnemy:
				return new IsEnemy()
			case IsNeighbor:
				return new IsNeighbor(-1)
			case IsOpen:
				return new IsOpen()
			default:
				return new IsOpen()
		}
	}

	static Query getRandomQuery()
	{
		def q = VALUES.get(RANDOM.nextInt(SIZE))
		return q.query
	}
}


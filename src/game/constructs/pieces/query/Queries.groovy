package game.constructs.pieces.query

import game.constructs.condition.PreCondition
import game.constructs.pieces.Move

/**
 * @author Lawrence Thatcher
 *
 * Enum class that is a list of known queries, and getters for creating the appropriate Query object
 */
enum Queries
{
	IsEnemy,
	IsNeighbor,
	IsOpen,
	InARow

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
			case InARow:
				return new InARow(3)
			default:
				return new IsOpen()
		}
	}

	boolean isGlobal()
	{
		switch (this)
		{
			case IsOpen:
			case InARow:
				return true
			default:
				return false
		}
	}

	static Query getRandomQuery(List<List<Query>> preconditions, int n)
	{
		def sections = Move.sections(preconditions)
		def valid_sections = []
		for (int sec : sections)
			if (sec > n)
				valid_sections.add(sec - preconditions.size())

		def q = VALUES.get(RANDOM.nextInt(SIZE))
		while (!((valid_sections.size() > 0) || q != IsNeighbor))
			q = VALUES.get(RANDOM.nextInt(SIZE))

		if (q != IsNeighbor)
			return q.query

		int idx = valid_sections.get(RANDOM.nextInt(valid_sections.size())) as int
		return new IsNeighbor(idx)
	}

	static Query getRandomGlobalQuery()
	{
		def q = VALUES.get(RANDOM.nextInt(SIZE))
		while (!q.global)
			q = VALUES.get(RANDOM.nextInt(SIZE))
		return q.query
	}
}


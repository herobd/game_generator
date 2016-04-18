package game.constructs.condition.result

import game.constructs.pieces.query.Query

/**
 * @author Lawrence Thatcher
 */
enum EndGameResult implements PostCondition
{
	Win,
	Lose,
	Draw

	private static final Random RANDOM = new Random()
	private static final List<EndGameResult> VALUES = Collections.unmodifiableList(Arrays.asList(values())) as List<EndGameResult>
	private static final int SIZE = values().size()

	static EndGameResult getRandom()
	{
		return VALUES.get(RANDOM.nextInt(SIZE))
	}
}




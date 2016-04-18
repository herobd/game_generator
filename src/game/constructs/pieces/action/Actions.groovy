package game.constructs.pieces.action

import game.constructs.pieces.Move
import game.constructs.pieces.query.Queries

/**
 * @author Lawrence Thatcher
 */
enum Actions
{
	Capture,
	Mark,
	MoveToSelected

	Action getAction(int n)
	{
		switch (this)
		{
			case Capture:
				return new Capture(n)
			case Mark:
				return new Mark(n)
			case MoveToSelected:
				return new MoveToSelected(n)
			default:
				return new Mark(n)
		}
	}

	private static final Random RANDOM = new Random()
	private static final List<Actions> VALUES = Collections.unmodifiableList(Arrays.asList(values())) as List<Actions>
	private static final int SIZE = values().size()

	static Action getRandomAction(List<List<Queries>> preconditions)
	{
		def sections = Move.sections(preconditions)
		int idx = sections.get(RANDOM.nextInt(sections.size())) as int
		def a = VALUES.get(RANDOM.nextInt(SIZE))
		return a.getAction(idx)
	}
}
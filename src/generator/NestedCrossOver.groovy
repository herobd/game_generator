package generator

/**
 * @author Lawrence Thatcher
 */
class NestedCrossOver extends CrossOver
{
	Iterable<Gene> mine
	Iterable<Gene> other

	NestedCrossOver(Iterable<Gene> mine, Iterable<Gene> other)
	{
		this.mine = mine
		this.other = other
	}

	def call(args)
	{
		for (def x : mine)
		{
			for (def y : other)
			{
				if (x.compatible(y))
				{
					x.crossOver(y)
				}
			}
		}
	}

	@Override
	String toString()
	{
		return mine.toString() + other.toString()
	}
}

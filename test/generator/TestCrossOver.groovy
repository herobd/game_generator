package generator

import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestCrossOver
{
	List<String> left
	List<String> right

	@Before
	void initializeLists()
	{
		left = ['A', 'B', 'C']
		right = ['X', 'Y', 'Z']
	}

	@Test
	void test_GeneCross()
	{
		assert left == ['A', 'B', 'C']
		def c = {int i -> left[i] = right[i]}
		CrossOver g = new CrossOver(c)
		g(1)
		assert left == ['A', 'Y', 'C']
		assert right == ['X', 'Y', 'Z']
	}

	@Test
	void test_GeneCross__fromFunction()
	{
		assert left == ['A', 'B', 'C']
		cross(['Q', 'R'])
		assert left == ['A', 'R', 'C']
	}

	@Test
	void test_CrossOver__mateAsParameter()
	{
		assert left == ['A', 'B', 'C']
		def c = {List<String> r -> left[1] = r[1]}
		CrossOver g = new CrossOver(c)
		g(right)
		assert left == ['A', 'Y', 'C']
		assert right == ['X', 'Y', 'Z']
		g(['a', 'b', 'c', 'd'])
		assert left == ['A', 'b', 'C']
	}

	@Test
	void test_CrossOver__indexAsVariable()
	{
		List<CrossOver> G = []
		for (int i = 0; i < left.size(); i++)
		{
			def c = {int j, List<String> r -> left[j] = r[j]}
			CrossOver g = new CrossOver(c.curry(i))
			G.add(g)
		}
		assert left == ['A', 'B', 'C']
		G[0](right)
		assert left == ['X', 'B', 'C']
		G[1](['a', 'b'])
		assert left == ['X', 'b', 'C']
		G[2](right)
		assert left == ['X', 'b', 'Z']
	}


	void cross(List<String> RIGHT)
	{
		def c = {int i -> left[i] = RIGHT[i]}
		CrossOver g = new CrossOver(c)
		g(1)
	}
}

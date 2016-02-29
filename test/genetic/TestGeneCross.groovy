package genetic

import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestGeneCross
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
		GeneCross g = new GeneCross(c)
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

	void cross(List<String> RIGHT)
	{
		def c = {int i -> left[i] = RIGHT[i]}
		GeneCross g = new GeneCross(c)
		g(1)
	}
}

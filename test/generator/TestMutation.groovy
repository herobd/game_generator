package generator

import org.junit.Before
import org.junit.Test

/**
 * @author Lawrence Thatcher
 *
 * Tests for the Mutation class
 */
class TestMutation
{
	private String response

	@Before
	void setup()
	{
		this.response = "goodbye"
	}

	@Test
	void test_mutation()
	{
		Mutation m = new Mutation("hello", this)
		assert this.response == "goodbye"
		m()
		assert this.response == "hello world"
	}


	String hello()
	{
		this.response = "hello world"
		return "hello world"
	}
}

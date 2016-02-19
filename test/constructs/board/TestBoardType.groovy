package constructs.board

import org.junit.Test

/**
 * @author Lawrence Thatcher
 */
class TestBoardType
{
	@Test
	void testHierarchy()
	{
		assert BoardType.Grid.Square.parent == BoardType.Grid
	}
}

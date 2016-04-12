package game.constructs.board

import game.constructs.condition.functions.Function
import game.constructs.condition.functions.Supports
import game.gdl.clauses.GDLClause
//import game.gdl.clauses.HasClausesWithDep
import generator.FineTunable
import game.constructs.pieces.Piece
import game.constructs.player.Players

/**
 * @author Lawrence Thatcher
 *
 * Stores information about the game board.
 */
abstract class Board implements  Supports, FineTunable //HasClausesWithDep
{
	/**
	 * Retrieves the GDL-description and implementation of a particular function
	 * relative to the current game board. If that function is not supported by
	 * the current setup, this method will throw a FunctionNotSupportedException.
	 *
	 * @param func The function to retrieve the implementation for.
	 * @return A GDL clause giving the description of that function.
	 */
	GDLClause getImplementation(Function func)
	{
		try
		{
			def clause = func.retrieveBoardGDL(this)
			return clause
		}
		catch (MissingMethodException ex)
		{
			throw new FunctionNotSupportedException(func, ex)
		}
	}
	
	

	//@Override
	abstract Collection<GDLClause> getGDLClauses(List<Piece> pieces, Players players)

	@Override
	boolean supports(Function function)
	{
		try
		{
			function.retrieveBoardGDL(this)
			return true
		}
		catch (MissingMethodException ignore)
		{
			return false
		}
	}
	
	abstract String getGeneralSpaceGDL()
	abstract String getGeneralSpaceGDLIndex(int i)
	List<String> getPieceSpaceGDL() {return getSelectedSpaceGDL(0)}
	abstract List<String> getSelectedSpaceGDL(int i)
	
	//abstract List<List<GString>> getSelectedSpaceNborsGDL(int i,Set<GString> definitions);
	//abstract List<List<GString>> getSelectedSpaceI_NborsGDL(int i,Set<GString> definitions);
}

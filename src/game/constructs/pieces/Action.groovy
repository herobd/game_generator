package game.constructs.pieces

import game.constructs.board.Board
import generator.FineTunable

interface Action extends FineTunable
{
    
    /**
	 * Retrieves the GDL-description of the actual outcomes of the action
	 * like "(cell ?mTo ?nTo ${GameToken.PLAYER_NAME}"+"_"+piece_id+")"
	 *
	 * @param board The board for this game.
	 * @param n This is the nth postcondition (used for creating unique variable names if needed)
	 * @param piece_id The name of the parent piece
	 * @return A string for the appropriate GDL.
	 */
    String effect(Board board, int n, String piece_id);
    
    /**
	 * Retrieves the GDL variables of the spaces this action effects
	 * 
	 * @param board The board for this game.
	 * @param n This is the nth postcondition (used for creating unique variable names if needed)
	 * @return A list of strings of GDL variables. Lenght depends on board type
	 */
    List< List<String> > effected(Board board, int n);
    
    /**
	 * Retrieves the GDL variables of the action the player chooses.
	 * This is probably the same as effected().
	 * 
	 * @param board The board for this game.
	 * @param n This is the nth postcondition (used for creating unique variable names if needed)
	 * @return A list of strings of GDL variables. Lenght depends on board type
	 */
    Set< List<String> > params(Board board, int n);
    
    int complexityCount();
}

package game.constructs.pieces.action

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
    GString effect(Map<GString,GString> cells, Board board, String piece_id,Set<GString> definitions);
    
    /**
	 * Retrieves the GDL variables of the spaces this action effects
	 * 
	 * @param board The board for this game.
	 * @param n This is the nth postcondition (used for creating unique variable names if needed)
	 * @return A set of strings of GDL variables. Lenght depends on board type
	 */
    Set< List<GString> > effected(Board board, Set<GString> definitions);
    
    
    int complexityCount();
}

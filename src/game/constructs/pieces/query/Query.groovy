package game.constructs.pieces.query

import game.constructs.board.Board
import generator.FineTunable
import game.gdl.clauses.GDLClause

interface Query extends FineTunable
{
    
    /**
	 * Retrieves the GDL-description of the actual outcomes of the action
	 * like "(cell ?mTo ?nTo ${GameToken.PLAYER_NAME}"+"_"+piece_id+")"
	 *
	 * @param board The board for this game.
	 * @param piece_id The name of the parent piece
	 * @param n This a nth level condition, meaning it must reference the parameter n at least once and can only reference paramters <= n
	 * @return A string for the appropriate GDL.
	 */
    GString toGDL(Board board,String piece_id, int n);
    
    /**
	 * Retrieves rules that this defines that may be reused later (hence global)
	 *
	 * @param globalRules The global rules mapped by name to prevent duplications
	 * @param board The board for this game.
	 */
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board);
    
    int complexityCount();
    
    String convertToJSON()
}

package game.constructs.pieces.query


import game.constructs.board.Board
import game.constructs.condition.functions.Function
import game.constructs.pieces.query.Query
import game.gdl.statement.GameToken
import game.gdl.clauses.GDLClause

class IsEnemy implements Query
{
    
    @Override
    GString toGDL(Board board,String piece_id, int n)
    {
        return "(true (cell ${board.getSelectedSpaceGDL(n).join(' ')} ?pieceIsEnemy)) \
                (not (${GameToken.PLAYER_MARK} ?pieceIsEnemy))"
    }
    
    @Override 
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board) {}
    
    int complexityCount()
    {
        return 1
    }
    
    @Override
    int getNumParams()
	{
        return 0
	}
	
	@Override
    void changeParam(int param, int amount)
    {
        
    }

	@Override
	Collection<Function> getFunctions() {
		return []
	}

	@Override
	def getGDL_Signature() {
		// TODO
		return null
	}

    @Override
    String toString()
    {
        return "IsEnemy"
    }
    
    @Override
    String convertToJSON()
    {
        return '{"query":"IsEnemy"}'
    }
    
    //@Override
    static Query fromJSON(def parsed)
    {
        return new IsEnemy()
    }
}

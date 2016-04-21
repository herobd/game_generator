package game.constructs.condition.functions

import game.constructs.condition.PreCondition
import game.gdl.clauses.GDLClause
import game.gdl.statement.SimpleStatement
import game.constructs.board.Board
/**
 * @author Lawrence Thatcher
 */

trait Function implements PreCondition {

	abstract GameFunction getType()

	abstract String getFunctionName()

	abstract GDLClause retrieveBoardGDL(Object)

	Collection<Function> getFunctions()
	{
		return [this]
	}
	
	@Override
	int getNumParams()
	{
	    return 0
	}
	
	@Override
    void changeParam(int param, int amount){}
    
    @Override
    String convertToJSON()
    {
        return '{"function":"'+getFunctionName()+'"}'
    }
    
    @Override
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board) {}
    
    /*@Override
    GDLClause getGDLClauses()
    {
        return new SimpleStatement("filler for funtion: "+getFunctionName());
    }*/
}

package game.constructs.condition.functions

import game.constructs.condition.Condition
import game.gdl.clauses.GDLClause
import game.gdl.statement.SimpleStatement

/**
 * @author Lawrence Thatcher
 */
trait Function implements Condition {
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
    
    /*@Override
    GDLClause getGDLClauses()
    {
        return new SimpleStatement("filler for funtion: "+getFunctionName());
    }*/
}

package game.constructs.condition.functions

import game.constructs.condition.Condition
import game.gdl.clauses.GDLClause

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
}

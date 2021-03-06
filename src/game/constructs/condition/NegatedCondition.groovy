package game.constructs.condition

import game.constructs.condition.functions.Function
import game.constructs.pieces.query.IsOpen
import game.gdl.clauses.GDLClause
import game.constructs.board.Board

/**
 * @author Lawrence Thatcher
 *
 * Object representing a simple negated condition.
 */
class NegatedCondition implements PreCondition
{
	private PreCondition condition

	/**
	 * Takes in a condition and negates it.
	 * The condition can be either another combined condition or a Function itself.
	 * @param condition A Condition to negate.
	 */
	NegatedCondition(PreCondition condition)
	{
		this.condition = condition
	}

	PreCondition getCondition()
	{
		return condition
	}

	@Override
	Collection<Function> getFunctions()
	{
		return condition.functions
	}

	@Override
	String toString()
	{
		String result = "not "
		if (condition instanceof Function || condition instanceof IsOpen)
			result += condition.toString()
		else
			result += "(" + condition.toString() + ")"
		return result
	}
	
	@Override
	String convertToJSON()
	{
	    return '{"not":'+condition.convertToJSON()+'}'
	}

	@Override
	def getGDL_Signature()
	{
		def result = "not "
		if (condition instanceof Function || condition instanceof IsOpen)
			result += condition.GDL_Signature
		else
			result += "(" + condition.GDL_Signature + ")"
		return result
	}

	@Override
	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (getClass() != o.class)
			return false

		NegatedCondition that = (NegatedCondition) o
		if (condition != that.condition)
			return false

		return true
	}

	@Override
	int hashCode()
	{
		return this.toString().hashCode()
	}

	@Override
	NegatedCondition clone()
	{
		return new NegatedCondition(condition)
	}
	
	@Override
	int getNumParams()
	{
	    return condition.getNumParams()
    }
    
    @Override
    void changeParam(int param, int amount)
    {
        condition.changeParam(param,amount)
    }
    
    @Override
    int complexityCount()
    {
        return condition.complexityCount()
    }
    
    /*@Override
    GDLClause getGDLClauses()
    {
        return new SimpleStatement("filler for funtion: "+getFunctionName());
    }*/
    
    @Override
    void setGlobalRules(Map<String,GDLClause> globalRules, Board board)
    {
        condition.setGlobalRules(globalRules,board)
    }
}

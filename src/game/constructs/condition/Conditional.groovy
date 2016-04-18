package game.constructs.condition

import game.constructs.condition.functions.Function
import game.constructs.condition.result.EndGameResult
import game.constructs.condition.result.PostCondition
import game.constructs.pieces.Piece
import game.constructs.pieces.query.Queries
import generator.CrossOver
import generator.FineTunable
import generator.Gene
import generator.Mutation


/**
 * @author Lawrence Thatcher
 *
 * A class for representing game state conditionals, that consist of a condition (antecedent), and a result (consequent)
 */
class Conditional implements FineTunable, Gene
{
	private PreCondition antecedent
	private PostCondition consequent

	Conditional(PreCondition condition, PostCondition result)
	{
		this.antecedent = condition
		this.consequent = result
	}

	Conditional()
	{
		this.antecedent = Queries.randomGlobalQuery
		this.consequent = EndGameResult.random
	}

	/**
	 * Retrieves the game state condition that this conditional rests upon
	 * @return a Function type
	 */
	PreCondition getAntecedent()
	{
		return antecedent
	}

	/**
	 * Retrieves the action or result that happens when the game state condition is met
	 * @return a PostCondition type
	 */
	PostCondition getConsequent()
	{
		return consequent
	}

	/**
	 * Retrieves a collection of all the Functions that are used in the antecedent
	 * @return a list or set of Functions
	 */
	Collection<Function> getFunctions()
	{
		return antecedent.functions
	}

	@Override
	String toString()
	{
		String result = antecedent.toString()
		result += " -> "
		result += consequent.toString()
		return result
	}
	
	String convertToJSON()
	{
	    return '{"antecedent":'+antecedent.convertToJSON()+', "consequent":"end game['+consequent+']"}'
	}

	@Override
	boolean equals(o)
	{
		if (this.is(o))
			return true
		if (!(o instanceof Conditional))
			return false

		Conditional that = (Conditional) o
		if (antecedent != that.antecedent)
			return false
		if (consequent != that.consequent)
			return false

		return true
	}

	@Override
	int hashCode()
	{
		int result
		result = antecedent.hashCode()
		result = 31 * result + (consequent != null ? consequent.hashCode() : 0)
		return result
	}

	@Override
	Conditional clone()
	{
		return new Conditional(antecedent, consequent)
	}

    int complexityCount()
    {
        //TODO
        return 1
    }
    
    @Override
    int getNumParams()
	{
	    
	    int ret=0
	    ret+=antecedent.getNumParams()
	    //ret+=consequent.getNumParams() //TODO will this just reference somthing else I'll already see?
        return ret
	}
	
    @Override
    void changeParam(int param, int amount)
    {
        int sofar=param
        if (sofar-antecedent.getNumParams()<0)
        {
            antecedent.changeParam(sofar,amount)
            return
        }
        else
            sofar-=antecedent.getNumParams()
        
        //if (sofar-consequent.getNumParams()<0)
        //{
        //    consequent.changeParam(sofar,amount)
        //    return
        //}
        //else
        //    sofar-=consequent.getNumParams()
            
    }

	@Override
	List<CrossOver> getPossibleCrossOvers(Gene other)
	{
		other = other as Conditional
		def result = []
		def c = {Conditional cc -> this.antecedent = cc.antecedent}
		c.curry(other)
		result.add(new CrossOver(c))

		c = {Conditional cc -> this.consequent = cc.consequent}
		c.curry(other)
		result.add(new CrossOver(c))

		return result
	}

	@Override
	List<Mutation> getPossibleMutations()
	{
		def result = parameterMutations

		result.add(mutationMethod("negateAntecedent"))

		return result
	}

	void negateAntecedent()
	{
		if (antecedent instanceof NegatedCondition)
			antecedent = antecedent.condition
		else
			antecedent = new NegatedCondition(antecedent)
	}

}

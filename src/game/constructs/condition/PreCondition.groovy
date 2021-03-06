package game.constructs.condition

import game.constructs.condition.functions.Function
import generator.FineTunable
import game.gdl.clauses.GDLClause
import game.constructs.board.Board

/**
 * @author Lawrence Thatcher
 *
 * Represents a state condition that is the antecedent of a Conditional.
 * This can either be a logic conjunction, disjunction, or negation of a predicate (Function),
 * or can be a function in and of itself.
 */

interface PreCondition extends FineTunable
{
	/**
	 * Returns a collection of the Functions that are used in this condition.
	 * If the condition is a single Function, then that function returns a list containing only itself.
	 * @return a set or list of Function objects
	 */
	Collection<Function> getFunctions()

	/**
	 * Should provide a custom implementation of toString()
	 */
	String toString()
	
	String convertToJSON()

	/**
	 * Converts the Condition string representation to a valid GDL format (since these might not always be the same)
	 * @return A String representing the GDL encoding of the condition
	 */
	def getGDL_Signature()
	
	int complexityCount()
	
	//GDLClause getGDLClauses()
	
	void setGlobalRules(Map<String,GDLClause> globalRules, Board board)
}

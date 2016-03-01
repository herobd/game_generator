package game.gdl.clauses

/**
 * @author Lawrence Thatcher
 *
 * Interface: says object can return a set of GDL clauses
 */
interface HasClauses
{
	/**
	 * Property that gets all the respective GDL Clauses associated with this construct.
	 * @return A list or set of GDLClauses
	 */
	Collection<GDLClause> getGDLClauses()
}
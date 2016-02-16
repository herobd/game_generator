package gdl

/**
 * @author Lawrence Thatcher
 *
 * Interface: says object can return a set of GDL clauses
 */
interface HasClauses
{
	Collection<GDLClause> getGDLClauses()
}
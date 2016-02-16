package gdl

/**
 * @author Lawrence Thatcher
 */
interface GDLClause
{
	ClauseType getClauseType()

	String toGDLString()
}
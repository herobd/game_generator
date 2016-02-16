package gdl

/**
 * @author Lawrence Thatcher
 *
 * A clause represents a section in the GDL code, which can have multiple statements in it
 */
interface GDLClause
{
	ClauseType getClauseType()

	List<GDLStatement> getStatements()

	String toGDLString()
}
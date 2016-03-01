package game.gdl.clauses

import game.gdl.statement.GDLStatement

/**
 * @author Lawrence Thatcher
 *
 * A clause is a group of related statements, which are divided into groups of type for readability
 * TODO: add notion of sub-clause later?
 */
interface GDLClause
{
	ClauseType getClauseType()

	List<GDLStatement> getStatements()

	String toGDLString()
}
package gdl.clauses

import gdl.GDLStatement

/**
 * @author Lawrence Thatcher
 */
abstract class AbstractClause implements GDLClause
{
	protected ClauseType type
	protected List<GDLStatement> statementList

	AbstractClause(ClauseType type, List<GDLStatement> statements)
	{
		this.type = type
		this.statementList = statements
	}

	@Override
	List<GDLStatement> getStatements()
	{
		return statementList
	}

	@Override
	ClauseType getClauseType()
	{
		return this.type
	}

	@Override
	String toGDLString()
	{
		String result = ""
		for (def s : statementList)
		{
			result += s.toString()
			result += "\n"
		}
		return result
	}
}

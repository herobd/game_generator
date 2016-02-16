package gdl

/**
 * @author Lawrence Thatcher
 */
abstract class AbstractClause implements GDLClause
{
	protected ClauseType type
	protected List<GDLStatement> statementList

	AbstractClause(ClauseType type, List<String> statements)
	{
		this.type = type
		for (String s : statements)
		{
			GDLStatement g = new GDLStatement(s)
			statementList.add(g)
		}
	}

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

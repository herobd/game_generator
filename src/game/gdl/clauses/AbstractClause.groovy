package game.gdl.clauses

import game.gdl.GDLStatement

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

	boolean contains(GDLStatement statement)
	{
		for (def s : this.statements)
		{
			if (s == statement)
				return true
		}
		return false
	}

	boolean contains(String statement)
	{
		return this.contains(new GDLStatement(statement))
	}

	void join(GDLClause c)
	{
		this.statementList += c.statements
	}
}

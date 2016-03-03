package game.gdl.clauses

import game.GameContextInfo
import game.gdl.statement.SimpleStatement
import game.gdl.statement.StatementFactory

/**
 * @author Lawrence Thatcher
 */
abstract class AbstractClause implements GDLClause
{
	protected ClauseType type
	protected List<SimpleStatement> statementList

	AbstractClause(ClauseType type, List<SimpleStatement> statements)
	{
		this.type = type
		this.statementList = statements
	}

	@Override
	List<SimpleStatement> getStatements()
	{
		return statementList
	}

	@Override
	ClauseType getClauseType()
	{
		return this.type
	}

	@Override
	String toGDLString(GameContextInfo contextInfo)
	{
		def allStatements = StatementFactory.interpolateStatements(statements, contextInfo)
		String result = ""
		for (def s : allStatements)
		{
			result += s.toString()
			result += "\n"
		}
		return result
	}

	boolean contains(SimpleStatement statement)
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
		return this.contains(new SimpleStatement(statement))
	}

	void join(GDLClause c)
	{
		this.statementList += c.statements
	}
}

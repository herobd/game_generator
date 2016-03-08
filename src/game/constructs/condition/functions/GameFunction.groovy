package game.constructs.condition.functions

import game.constructs.condition.Condition
import game.gdl.clauses.GDLClause
import game.gdl.statement.Tokens

/**
 * @author Lawrence Thatcher
 */
enum GameFunction implements Function, Condition
{
	N_inARow("in_a_row", {n -> n.toString() + "inARow"}, "${Tokens.PLAYER_MARK}"),
	Open("open");

	private Closure fn
	private String funcName
	private GString signature = null

	private GameFunction(String f)
	{
		funcName = f
		this.fn = null
	}

	private GameFunction(String f, Closure c)
	{
		funcName = f
		this.fn = c
	}

	private GameFunction(String f, Closure c, GString signature)
	{
		funcName = f
		this.fn = c
		this.signature = signature
	}

	def call(args)
	{
		if (this.fn == null)
			return this
		ParametrizedFunction f
		if (this.signature == null)
			f = new ParametrizedFunction(this.fn, this)
		else
			f = new ParametrizedFunction(this.fn, this, this.signature)
		f(args)
		return f
	}

	int getNumParams()
	{
		if (this.fn == null)
			return 0
		return this.fn.maximumNumberOfParameters
	}

	@Override
	String getFunctionName()
	{
		return funcName
	}

	@Override
	GDLClause retrieveBoardGDL(def obj)
	{
		if (this.numParams == 0)
			return (GDLClause){x -> x."$functionName"()}.call(obj)
		else
			return (GDLClause){x -> x."$functionName"(defaultArgument)}.call(obj)
	}

	@Override
	GameFunction getType()
	{
		return this
	}

	@Override
	String toString()
	{
		return this.funcName
	}

	def getGDL_Signature()
	{
		return this.funcName
	}

	private Object getDefaultArgument()
	{
		switch (this)
		{
			case N_inARow:
				return 3
			default:
				return null
		}
	}
}
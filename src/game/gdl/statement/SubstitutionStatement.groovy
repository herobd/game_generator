package game.gdl.statement

/**
 * @author Lawrence Thatcher
 *
 * A GDL statement that substitutes values into the statement that the provider did not have access to.
 */
class SubstitutionStatement extends InterpolatedStatement implements GDLStatement
{
	SubstitutionStatement(GString statement)
	{
		super(statement)
	}

	@Override
	StatementType getType()
	{
		return StatementType.Substitution
	}
}

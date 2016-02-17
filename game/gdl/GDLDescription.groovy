package gdl

/**
 * @author Lawrence Thatcher
 *
 * A class for representing the GDL description of a game
 */
class GDLDescription
{
	//TODO: add gameName
	private List<GDLClause> roles = []
	private List<GDLClause> baseAndInput = []
	private List<GDLClause> initialState = []
	private List<GDLClause> dynamicComponents = []
	private List<GDLClause> legal = []
	private List<GDLClause> goal = []
	private List<GDLClause> terminal = []

	private static final String ROLES_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
			";; Roles\n" +
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	private static final String BASE_INPUT_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
			";; Base & Input\n" +
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	private static final String INITIAL_STATE_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
			";; Initial State\n" +
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	private static final String DYNAMIC_COMPONENTS_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n" +
			";; Dynamic Components\n" +
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	private static final String LEGAL_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	private static final String GOAL_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	private static final String TERMINAL_HEADER =
			";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n"

	GDLDescription(List<GDLClause> clauses)
	{
		for (GDLClause clause : clauses)
		{
			switch (clause.clauseType)
			{
				case ClauseType.Roles:
					this.roles.add(clause)
					break
				case ClauseType.BaseAndInput:
					this.baseAndInput.add(clause)
					break
				case ClauseType.InitialState:
					this.initialState.add(clause)
					break
				case ClauseType.DynamicComponents:
					this.dynamicComponents.add(clause)
					break
				case ClauseType.Legal:
					this.legal.add(clause)
					break
				case ClauseType.Goal:
					this.goal.add(clause)
					break
				case ClauseType.Terminal:
					this.terminal.add(clause)
					break
			}
		}
	}

	@Override
	String toString()
	{
		String result = ""

		result += ROLES_HEADER
		for (def role : roles)
			result += role.toGDLString() + "\n"
		result += BASE_INPUT_HEADER
		for (def base : baseAndInput)
			result += base.toGDLString() + "\n"
		result += INITIAL_STATE_HEADER
		for (def init : initialState)
			result += init.toGDLString() + "\n"
		result += DYNAMIC_COMPONENTS_HEADER
		for (def comp : dynamicComponents)
			result += comp.toGDLString() + "\n"
		result += LEGAL_HEADER
		for (def l : legal)
			result += l.toGDLString() + "\n"
		result += GOAL_HEADER
		for (def g : goal)
			result += g.toGDLString() + "\n"
		result += TERMINAL_HEADER
		for (def t : terminal)
			result += t.toGDLString() + "\n"
		return result
	}
}

package game.gdl

import game.GameContextInfo
import game.gdl.clauses.ClauseType
import game.gdl.clauses.GDLClause

/**
 * @author Lawrence Thatcher
 *
 * A class for representing the GDL description of a game
 */
class GDLDescription
{
	private String name
	private GameContextInfo context

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

	GDLDescription(String name, List<GDLClause> clauses, GameContextInfo context)
	{
		this.name = name
		this.context = context

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
			result += role.toGDLString(context) + "\n"
		result += BASE_INPUT_HEADER
		for (def base : baseAndInput)
			result += base.toGDLString(context) + "\n"
		result += INITIAL_STATE_HEADER
		for (def init : initialState)
			result += init.toGDLString(context) + "\n"
		result += DYNAMIC_COMPONENTS_HEADER
		for (def comp : dynamicComponents)
			result += comp.toGDLString(context) + "\n"
		result += LEGAL_HEADER
		for (def l : legal)
			result += l.toGDLString(context) + "\n"
		result += GOAL_HEADER
		for (def g : goal)
			result += g.toGDLString(context)
		result += TERMINAL_HEADER
		for (def t : terminal)
			result += t.toGDLString(context) + "\n"
		return result
	}

	//TODO: replace with more general version (that supports statement generators, etc..) such as 'containsRole()'
	def getRolesClauses()
	{
		return this.roles
	}
}

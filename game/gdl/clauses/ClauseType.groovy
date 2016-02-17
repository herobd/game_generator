package gdl.clauses

/**
 * @author Lawrence Thatcher
 *
 * Enum specifying the region of GDL that a given clause belongs to
 */
enum ClauseType
{
	GameName,
	Roles,
	BaseAndInput,
	InitialState,
	DynamicComponents,
	Legal,
	Goal,
	Terminal
}
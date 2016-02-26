package constructs.player

/**
 * @author Lawrence Thatcher
 *
 * A simple list of potential names of players
 */
enum PlayerName
{
	Unknown,

	//Colors
	White,
	Black,
	Red,
	Blue,
	Yellow,
	Green,
	Magenta,
	Teal,
	Orange,
	Turquoise,
	Pink,
	Purple,
	Cyan,
	Azure,
	Brown,
	Crimson,
	Grey,
	Fuchsia,
	Silver,
	Gold,
	Khaki,
	Lime,
	Plum,
	Beige,
	Aqua,
	Lavender,
	Olive,
	Salmon,

	// Tic-Tac-Toe-ish
	xPlayer("x"),
	oPlayer("o"),
	yPlayer("y"),
	tPlayer("t"),

	// Entity
	Robot,
	Human,
	Dinosaur,
	Animal,
	Alien,
	Ghost;

	private static final List<PlayerName> VALUES = Collections.unmodifiableList(Arrays.asList(values())) as List<PlayerName>
	private static final Random RAND = new Random()
	private static final int SIZE = values().size()

	private String mark_token = null

	private PlayerName()
	{}

	private PlayerName(String token_mark)
	{
		this.mark_token = token_mark
	}

	String getMarkToken()
	{
		return mark_token
	}

	Player toPlayer()
	{
		return new Player(this.toString(), this.markToken)
	}

	//Static Methods
	static PlayerName toPlayerName(String name)
	{
		for (PlayerName p : VALUES)
		{
			if (p.matches(name))
				return p
		}
		return Unknown
	}

	static PlayerName getRandom()
	{
		return VALUES.get(RAND.nextInt(SIZE))
	}

	static boolean contains(String name)
	{
		for (PlayerName p : VALUES)
		{
			if (p.matches(name))
				return true
		}
		return false
	}

	//Helper Methods
	private boolean matches(String name)
	{
		return this.toString().toLowerCase() == name.toLowerCase()
	}
}
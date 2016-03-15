package game.gdl.statement

/**
 * @author Lawrence Thatcher
 *
 * An enum listing the types of generation strategies supported.
 *
 * PerPlayer: generates one statement for every player
 * PerOtherPlayer: for every player generates N-1 statements (one for every player other than the current player),
 * 	where N is the number of players.
 */
enum GenerationStrategy
{
	PerPlayer,
	PerOtherPlayer
}
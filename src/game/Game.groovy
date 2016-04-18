package game

import game.constructs.condition.Conditional
import game.constructs.pieces.Move
import game.constructs.pieces.Pieces
import game.constructs.pieces.StartingPosition
import game.constructs.pieces.action.Mark
import game.constructs.pieces.query.IsOpen
import game.constructs.player.Player
import game.constructs.player.Players
import game.constructs.TurnOrder
import game.constructs.board.Board
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.TerminalConditional
import game.constructs.end.EndGameConditions
import game.constructs.pieces.NamedPieces
import game.constructs.pieces.Piece
import game.gdl.clauses.GDLClause
import game.gdl.GDLConvertable
import game.gdl.GDLDescription
import generator.Evolvable
import generator.Gene
import generator.FineTunable
import game.gdl.clauses.base.BaseClause
import game.gdl.statement.GeneratorStatement
import game.gdl.statement.GameToken
import groovy.json.JsonSlurper
import game.constructs.condition.NegatedCondition
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.result.EndGameResult
import game.constructs.condition.TerminalConditional
import game.constructs.pieces.query.Queries

/**
 * @author Lawrence Thatcher
 *
 * A class holding an abstract game description of a particular game.
 */
class Game implements Evolvable, GDLConvertable, FineTunable
{
	private static final double DEFAULT_CROSS_OVER_PROBABILITY = 0.1
	private static final Random RANDOM = new Random()

	private String name = "unnamed game"
	private String id = "no_id"
	private Players players
	private Board board
	private TurnOrder turnOrder
	private Pieces pieces
	private EndGameConditions end
	private double score=0
	
	private List<Evolvable> parents = []

	/**
	 *
	 * @param players a Players object, representing the players in the game
	 * @param board a Board object representing the game's board
	 * @param turnOrder the TurnOrder value, representing the turn-order pattern that will be followed in this game
	 * @param pieces a list of Piece objects representing the game pieces that will be used in this game.
	 * If none are specified, it will default to simple mark-and-place pieces, similar to the Ludi system.
	 * @param end a list of TerminalConditions that will be used to set when the game will end,
	 * as well as who the winner will be (if any).
	 */
	Game(Players players, Board board, TurnOrder turnOrder, List<Piece> pieces, List<TerminalConditional> end)
	{
		this.players = players
		this.board = board
		this.turnOrder = turnOrder
		if (pieces == null || pieces == [])
		{
			Move mark = new Move([[], [new IsOpen()]], [new Mark(1)])
			Piece basic = new Piece([new StartingPosition(0)], [mark])
			this.pieces = new Pieces([basic])
		}
		else
			this.pieces = new Pieces(pieces)
		this.end = new EndGameConditions(end, board)
		namePieces()
	}
	
	Game(Players players, Board board, TurnOrder turnOrder, List<Piece> pieces, List<TerminalConditional> end, double score)
	{
		this.players = players
		this.board = board
		this.turnOrder = turnOrder
		if (pieces == null || pieces == [])
			this.pieces = new Pieces([NamedPieces.DEFAULT_PIECE])
		else
			this.pieces = new Pieces(pieces)
		this.end = new EndGameConditions(end, board)
		this.score=score
		namePieces()
	}
	
	Game()
	{
	    this.turnOrder = TurnOrder.Alternating
	    this.players = new Players(Math.max(2,(int) (Math.round(RANDOM.nextGaussian() * 1) + 2)))
	    this.board = new SquareGrid(Math.max(2,(int) (Math.round(RANDOM.nextGaussian() * 4) + 6)))
	    this.end = new EndGameConditions([new TerminalConditional(new NegatedCondition(Queries.IsOpen.query), EndGameResult.Draw)],this.board)
	    
	    this.pieces = new Pieces(Math.max(1,(int) (Math.round(RANDOM.nextGaussian() * 3) + 1)))
	    
	    namePieces()
	}
	
	static Game fromJSON(String json)
	{
	    def jsonSlurper = new JsonSlurper()
        def parsed = jsonSlurper.parseText(json)
        def players = new Players(parsed.players)
        def board
        if (parsed.board.macroType=="Grid" && parsed.board.tileType=="Square" && parsed.board.layoutShape=="Square")
            board=new SquareGrid(parsed.board.size)
        else
            println 'Error, unsupported board: '+parsed.board
        def pieces=[]
        parsed.pieces.each { p ->
            pieces.push(Piece.fromJSON(p))
        }
        //default
        def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow([3]), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
        return new Game(players,board,TurnOrder.Alternating,pieces,end,100)
	}
	
	void namePieces()
	{
	    this.pieces.namePieces()
	}
	
	int getNumPlayers()
	{
	    return players.size()
	}
	
	String getId()
	{
	    return id
	}
	
	void setId(String id)
	{
	    this.id=id
	}
	
	String getName()
	{
	    return name
	}
	
	int getGDLVersion()
	{
	    return 1
	}

	@Override
	GDLDescription convertToGDL()
	{
		List<GDLClause> clauses = []
		clauses += players.GDLClauses
		clauses += board.getGDLClauses(pieces.pieces, players)
		clauses += turnOrder.GDLClauses
		Map<String,GDLClause> globalRules= new HashMap<String,GDLClause>() //This is to prevent duplications of rules
		for (Piece p : pieces)
		{
			clauses += p.getGDLClauses(globalRules,board)
		}
		end.setGlobalRules(globalRules)
		globalRules.each { name, rule ->
		    clauses += rule
		}
		clauses += end.supportedBoardGDLClauses
		clauses += end.GDLClauses

		GameContextInfo contextInfo = new GameContextInfo(players)
		return new GDLDescription(name, clauses, contextInfo)
	}
	
	String convertToJSON()
	{
	    List<String> ps = []
		for (Piece p : pieces)
		{
			ps.push(p.convertToJSON())
		}
		
		String ret = ''
		ret +='{\n'
		ret +='  "players": ["'+players.getPlayerNames().join('", "')+'"],\n'
		ret +='  "board": '+board.convertToJSON()+',\n'
		ret +='  "pieces": ['+ps.join(', ')+'],\n'
		ret +='  "end": '+end.convertToJSON()+'\n'
		ret +='}'
		return ret
	}

	@Override
	Evolvable crossOver(Evolvable mate)
	{
		Game child = this.clone()
		child.setParents(this,mate)
		List<List<Gene>> matchableGenes = [child.genes, mate.genes].transpose()
		for (def pair : matchableGenes)
		{
			Gene m = pair[0]
			Gene f = pair[1]
			m.crossOver(f)
		}
		child.namePieces()
		return child
	}
	
	void setParents(Evolvable temp, Evolvable other)
	{
	    parents.add(temp)
	    parents.add(other)
	}
	
	@Override
	List<Evolvable> getParents()
	{
	    return parents
	}

	@Override
	def mutate()
	{
		for (Gene gene : genes)
		{
			gene.mutate()
		}
	}

	@Override
	Game clone()
	{
		Players c_players = players.clone()
		Board c_board = (Board)board.clone()
		def c_pieces = []
		def c_end = []
		for (Piece p : pieces)
			c_pieces.add(p.clone())
		for (Conditional c : end.conditionals)
			c_end.add(c.clone())
		return new Game(c_players, c_board, turnOrder, c_pieces, c_end,score)
	}

	@Override
	List<Gene> getGenes()
	{
		List<Gene> result = [players, board, pieces, end]
		return result
	}

	@Override
	String toString()
	{
		String result = ""
		result += "Players: " + players.toString() + "\n"
		result += "Board: " + board.toString() + "\n"
		result += "TurnOrder: " + turnOrder.toString() + "\n"
		result += "Pieces: \n"

		// TODO: add Pieces class

		for (Piece p : pieces)
		{
			result += "\t" + p.toString() + "\n"
		}
		result += "End:\n"
		for (Conditional c : end.conditionals)
		{
			result += "\t" + c.toString() + "\n"
		}
		return result
	}

    int complexityCount_piece()
    {
        int ret=0
        for (Piece p : pieces)
            ret += 1 + p.complexityCount()
        return ret
    }
    int complexityCount_end()
    {
        int ret=0
        for (Conditional c : end.conditionals)
            ret += 1 + c.complexityCount()
        return ret
    }
    
    @Override
    double getScore()
    {
        return score
    }
    
    @Override
    void setScore(double score)
    {
        this.score=score
    }
    
    @Override
    int getNumParams()
    {
        def ret=0
        ret+=players.getNumParams();
        ret+=board.getNumParams();
        ret+=turnOrder.getNumParams();
		ret+=pieces.getNumParams();
        ret+=end.getNumParams();
        return ret
    }
    
    @Override
    void changeParam(int param, int amount)
    {
		int i = 0
		// Players
		if (param >= i+players.numParams)
			i+= players.numParams
		else
		{
			players.changeParam(param-i, amount)
			return
		}
		// Board
		if (param >= i+board.numParams)
			i+= board.numParams
		else
		{
			board.changeParam(param-i, amount)
			return
		}
		// Turn Order
		if (param >= i+turnOrder.numParams)
			i+= turnOrder.numParams
		else
		{
			turnOrder.changeParam(param-i, amount)
			return
		}
		// Pieces
		if (param >= i+pieces.numParams)
			i+= pieces.numParams
		else
		{
			pieces.changeParam(param-i, amount)
			return
		}
		// End
		if (param >= i+end.numParams)
			i+= end.numParams
		else
		{
			end.changeParam(param-i, amount)
		}
    }
}

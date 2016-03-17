package generator

import game.constructs.TurnOrder
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.NegatedCondition
import game.constructs.condition.TerminalConditional
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.result.EndGameResult
import game.constructs.player.Players
import game.Game
import generator.GeneratorClient

/**
 * A class that does an evolutionary algorithm.
 *
 * @author Lawrence Thatcher
 */
class EvolutionaryAlgorithm
{
	private List<Evolvable> population = []
	private GeneratorClient client = null
	private static final Random RANDOM = new Random()
	private Boolean cont=true
    private Boolean debug_sub=false

	EvolutionaryAlgorithm(List<Evolvable> initialPop, String controllerAddress)
	{
		population = initialPop
		client = new GeneratorClient(controllerAddress)
	}

	public static void main(String[] args)
	{
		//For now using a hard-coded initial population...
		def players = new Players(["White", "Black", "Salmon", "Pink"])
		def board = new SquareGrid(3, true)
		def end = []
		end.add(new TerminalConditional(GameFunction.N_inARow(3), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(GameFunction.Open), EndGameResult.Draw))
		Game p1 = new Game(players, board, TurnOrder.Alternating, [], end)

		players = new Players(["Red", "Orange", "Green", "Yellow", "Gold"])
		Game p2 = new Game(players, board, TurnOrder.Alternating, [], end)
        
        def controllerAddress="ironsides.cs.byu.edu:8080"
        if (args.length > 0)
            controllerAddress = args[0]
            
		EvolutionaryAlgorithm algorithm = new EvolutionaryAlgorithm([p1, p2],controllerAddress)

		int iters = 50
		if (args.length > 1)
			iters = new Integer(args[1])
		int itersTillLongEval = 100
		if (args.length > 2)
			itersTillLongEval = new Integer(args[2])
		double intrinsicScoreThresh = 0.5
		if (args.length > 3)
			intrinsicScoreThresh = new Integer(args[3])
		println "Doing " + Integer.toString(iters) + " iterations."

		algorithm.run(iters,itersTillLongEval,intrinsicScoreThresh)

		//algorithm.printPopulationMembers()
	}

	//TODO: shouldn't have to specify number of iterations...
	def run(int iterations, int tillLong, double intrinsicScoreThresh)
	{
	    int idGen=0
		for (int i = 0; i < iterations && cont; i++)
		{
		    for (int j = 0; j < tillLong && cont; j++)
		    {
			    //Parent Selection
			    def p1 = randomMember
			    def p2 = randomMember
			    while (p2 == p1)
				    p2 = randomMember

			    //Cross-Over and Mutation
			    def p3 = p1.crossOver(p2)
			    p3.mutate()
			    
			    //christen
			    p3.setId("testGame_"+(idGen++))

			    //Evaluation
			    // TODO: cull inbreds
			    //controller hook here
			    def intrinsicScore = instrinsicEvaluation(p3)
			    if (intrinsicScore>intrinsicScoreThresh)
			    {
			        debug_sub=true
			        def resp = client.doShortEvalAndFineTune(p3,intrinsicScore)
		        }
			    updateScores(client.getControllerResponses())

			    //Add to population
			    population.add(p3)
		    }
		    //client.doLongEval(topXFromPopulation())
		}
	}

	//Helper Methods
	void printPopulationMembers()
	{
		for (Evolvable member : population)
		{
			println member.toString()
			println "\n"
		}
	}

	private Evolvable getRandomMember()
	{
		//TODO: Use stochastic universal sampling to select fitter individuals more often
		int idx = RANDOM.nextInt(population.size())
		return population[idx]
	}
	
	private double instrinsicEvaluation(Game g)
	{
	    //TODO
	    if (debug_sub)
	        sleep(10000)
	    if (g.getNumPlayers()>3)
	        return 0
	    return 0.51
	}
	
	private void updateScores(List toUpdate)
	{
	    
	    for (def i=0; i<toUpdate.size(); i++)
	    {
	        def gameId = toUpdate[i].id
	        def score = toUpdate[i].score.evalScore
	        //You can access the elements of the score (for changing search strat) in the ${it}.score object
	        //also ${it}.score.id holds the identifier for the parameters used to create this score
	        
	        
	        //TODO, something with these
	        
	        //debugging
	        println 'Got score '+score+' for game '+gameId
	        cont=false
	        
	        
	    }
	}
}

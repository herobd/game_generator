package generator

//@Grab(group='com.google.guava', module='guava', version='14.0')

import game.constructs.TurnOrder
import game.constructs.board.grid.SquareGrid
import game.constructs.condition.NegatedCondition
import game.constructs.condition.TerminalConditional
import game.constructs.condition.functions.GameFunction
import game.constructs.condition.result.EndGameResult
import game.constructs.player.Players
import game.Game
import generator.GeneratorClient
import generator.InstrinsicEvaluator


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
    private Object params = null //This holds the wieghts for insrinsic evaulation and an id (for the params version), as well things like the probabilities for gene selection and so forth
    private InstrinsicEvaluator instrinsicEvaluator = null
    private Map fineTUning = [:]

	EvolutionaryAlgorithm(List<Evolvable> initialPop, String controllerAddress)
	{
		population = initialPop
		client = new GeneratorClient(controllerAddress)
		params = client.getLastParams()
		instrinsicEvaluator = new InstrinsicEvaluator(params)
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
			
		double intrinsicScoreThresh = 0.0
		if (args.length > 3)
			intrinsicScoreThresh = new Integer(args[3])
			
		println "Doing " + Integer.toString(iters) + " iterations."

		algorithm.run(iters,itersTillLongEval,intrinsicScoreThresh)

		//algorithm.printPopulationMembers()
	}

	//TODO: shouldn't have to specify number of iterations...
	def run(int iterations, int tillLong, double intrinsicScoreThresh)
	{
	    def idGen=0
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
			    //TODO make & set name
			    p3.setId("testGame_"+(idGen++))

			    //Evaluation
			    // TODO: cull inbreds
			    //controller hook here
			    def intrinsicScore = instrinsicEvaluator.evaluate(p3)
			    if (intrinsicScore>intrinsicScoreThresh)
			    {
			        debug_sub=true
			        fineTuneInit(p3,intrinsicScore,params.id);
		        }
		        def controllerResScores = client.getControllerResponses()
			    updateScores(controllerResScores)
			    fineTuneNext(controllerResScores)

			    //Add to population
			    population.add(p3)
		    }
		    //client.doLongEval(topXFromPopulation())
		}
	}
	
	void fineTuneInit(Game g, double intrinsicScore)
	{
	    def resp = client.doShortEval(g,intrinsicScore)
	    def ft = [
	                origId:g.getId(),
	                lastScore:-99999, //so the first run is always gets accepted
	                lastParam:-1,
	                currentVersion:g,
	                lastVersion:g,
	                iters:0,
	                iterOfLastImprovement:-1,
	                tried:'',
	                paramMap:[:]
                 ]
        fineTuning[g.getId()]=ft   
	}
	
	//The idea behind finetuning is thus:
	//We assume stochastic assent by randomly selecting a parameter and giving it +1 or -1
	//If it fails to improve the score, the reverse is tried (-/+ wise)
	//If we fail again, we select a new parameter
	//When a improvment occurs, we store whether increasing or descreasing that parameter was good
	//   to reuse in the event we select the parameter again
	//Then move on to a new parameter
	//We end when we've gone X iterations without a new improvment being found (a psuedo-maxima)
	void fineTuneNext(List toUpdate)
	{
	    for (def i=0; i<toUpdate.size(); i++)
	    {
	        def gameId = toUpdate[i].id
	        def score = toUpdate[i].score.evalScore
	        if (fineTuning.containsKey(gameId))
	        {
	            //do next step of finetuning
	            def ft = fineTuning.remove(gameId)
	            def numParams=ft.currentVersion.getNumParams()
	            if (score <= ft.lastScores)
	            {
	                if (ft.iters>params.shortFineTuneLimit && ft.scores.last()<params.shortFineTuneThresh ||
	                    ft.iters-ft.iterOfLastImprovement>params.fineTuneFamineLimit ||
	                    (ft.iters>=2 && numParams==1)
	                {
	                    if (ft.iters>2)//this is different enough
	                        population.add(ft.lastVersion)
	                    continue;//we're done fine tuning this game
	                }
	                ft.currentVersion=ft.lastVersion
	                
	                def nextParam=ft.lastParam
	                def plus
	                if (ft.tried=='-')
	                {
	                    plus=false
	                    
	                }
	                else if (ft.tried=='+')
	                {
	                    plus=true
	                }
	                else
	                {
	                    ft.paramMap.remove(ft.lastParam)
	                    //select new param
	                    
                        while (nextParam==ft.lastParam)
                        {
                            nextParam=RANDOM.nextInt(numParams)
                        } 
                        
                        if (ft.paramMap.containsKey(nextParam))
                        {
                            plus=ft.paramMap[nextParam]
                        }
                        else
                        {
                            plus = (RANDOM.nextInt(2)==0)
                        }
                        ft.tried=''
                            
                        
	                }
	                ft.paramMap[nextParam]=plus
	                if (plus)
	                {
	                    ft.tried+='+'
	                    ft.currentVersion.changeParam(nextParam,1)
                    }
                    else
                    {
                        ft.tried+='-'
                        ft.currentVersion.changeParam(nextParam,-1)
                    }
                    ft.lastParam=nextParam
	            }
	            else
	            {
	                ft.iterOfLastImprovement=ft.iters
	                //population.add(ft.currentVersion) moved to ending
	                ft.lastVersions=ft.currentVersion.clone()
	                ft.lastScore=score
	                
	                
                    //select a param
                    def nextParam=RANDOM.nextInt(numParams)
                    def plus
                    if (ft.paramMap.containsKey(nextParam))
                    {
                        plus=ft.paramMap[nextParam]
                    }
                    else
                    {
                        plus = (RANDOM.nextInt(2)==0)
                    }
                    
	                ft.paramMap[nextParam]=plus
	                if (plus)
	                {
	                    ft.tried='+'
	                    ft.currentVersion.changeParam(nextParam,1);
                    }
                    else
                    {
                        ft.tried='-'
                        ft.currentVersion.changeParam(nextParam,-1);
                    }
                    ft.lastParam=nextParam
	            }
	            
	            //
	            ft.iters++
	            ft.currentVersion.setId(ft.origId+'_$'+ft.iters+'$')
	            client.doShortEval(ft.currentVersion,ft.scores.last())
	            fineTuning[ft.currentVersion.getId()]=ft
	            //
	        }
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

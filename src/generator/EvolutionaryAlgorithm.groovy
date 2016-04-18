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

import game.constructs.condition.functions.Function
import game.constructs.condition.functions.ParametrizedFunction
import game.constructs.pieces.query.InARow
import game.constructs.pieces.query.Queries
import game.constructs.pieces.action.Mark
import game.constructs.pieces.action.MoveToSelected
import game.constructs.pieces.action.Capture
import game.constructs.pieces.query.IsOpen
import game.constructs.pieces.query.PieceOrigin
import game.constructs.pieces.query.IsNeighbor
import game.constructs.pieces.query.IsEnemy
import game.constructs.pieces.StartingPosition
import game.constructs.pieces.Piece
import game.constructs.pieces.Move

/**
 * A class that does an evolutionary algorithm.
 *
 * @author Lawrence Thatcher
 */
class EvolutionaryAlgorithm
{
	private Map<String,Evolvable> population = [:]
	private GeneratorClient client = null
	protected static final Random RANDOM = new Random()
	private Boolean cont=true
    private Boolean debug_sub=false
    private Object params = null //This holds the wieghts for insrinsic evaulation and an id (for the params version), as well things like the probabilities for gene selection and so forth
    private InstrinsicEvaluator instrinsicEvaluator = null
    private Map fineTuning = [:]
    private double maxScore=0
    private double minScore=0

	EvolutionaryAlgorithm(Map<String,Evolvable> initialPop, String controllerAddress)
	{
		population = initialPop
		client = new GeneratorClient(controllerAddress)
		params = client.getLastParams()
		instrinsicEvaluator = new InstrinsicEvaluator(params)
	}

	public static void main(String[] args)
	{
        
		//For now using a hard-coded initial population...
		def players = new Players(["White", "Black"])
		def board = new SquareGrid(3, true)
		def end = []
		end.add(new TerminalConditional(new InARow(3), EndGameResult.Win))
		end.add(new TerminalConditional(new NegatedCondition(Queries.IsOpen.query), EndGameResult.Draw))
		Move mark = new Move([[],[new IsOpen()]],[new Mark(1)])
		Piece basic = new Piece([new StartingPosition(0)],[mark])
		Game p1 = new Game(players, board, TurnOrder.Alternating, [basic], end)
        p1.setId("tictactoe")
        
        def players2 = new Players(["White", "Black"])
		def board2 = new SquareGrid(5, true)
		def end2 = []
		end2.add(new TerminalConditional(new InARow(4), EndGameResult.Win))
		end2.add(new TerminalConditional(new NegatedCondition(Queries.IsOpen.query), EndGameResult.Draw))
		Move move = new Move([[new PieceOrigin()],[new IsOpen(), new IsNeighbor(-1)],[new IsEnemy()]],[new MoveToSelected(1), new Capture(2)]);
		Piece starter = new Piece([new StartingPosition(StartingPosition.PositionType.Center,2)],[move]);
		Game p2 = new Game(players2, board2, TurnOrder.Alternating, [basic,starter], end2)
        p2.setId("starterGame")
		
        
        def controllerAddress="ironsides.cs.byu.edu:8080"
        if (args.length > 0)
            controllerAddress = args[0]

		def m = [:]
		m[p1.id] = p1
		m[p2.id] = p2
		for (int i=0; i<200; i++)
		{
		    Game randGame = new Game();
		    randGame.setId("initGame_"+i)
		    m[randGame.id] = randGame
		}
		EvolutionaryAlgorithm algorithm = new EvolutionaryAlgorithm(m, controllerAddress)

		int iters = 50
		if (args.length > 1)
			iters = new Integer(args[1])
			
		int itersTillLongEval = 200
		if (args.length > 2)
			itersTillLongEval = new Integer(args[2])
			
		double intrinsicScoreThresh = 0.0
		if (args.length > 3)
			intrinsicScoreThresh = new Integer(args[3])
			
		//println "Doing " + Integer.toString(iters) + " iterations."

		algorithm.run(iters,itersTillLongEval,intrinsicScoreThresh)

		//algorithm.printPopulationMembers()
	}

	//TODO: shouldn't have to specify number of iterations...
	def run(int iterations, int tillLong, double intrinsicScoreThresh)
	{
	    def idGen=0
		for (int i = 0; i < iterations || cont; i++)
		{
		    for (int j = 0; j < tillLong && cont; j++)
		    {
		        try
		        {
		            //println "making evoGame_"+(idGen++)
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
			        p3.setId("evoGame_"+(idGen++))

			        //Evaluation
			        // TODO: cull inbreds
			        //controller hook here
			        //println "ieval this evoGame_"+(idGen++)
			        def intrinsicScore = instrinsicEvaluator.evaluate(p3 as Game)
			        
			        if (intrinsicScore>maxScore)
	                    maxScore=intrinsicScore
                    if (intrinsicScore<minScore)
                        minScore=intrinsicScore	
			        if (intrinsicScore>intrinsicScoreThresh)
			        {
			            debug_sub=true
			            //println 'iscore '+intrinsicScore
			            fineTuneInit(p3 as Game,intrinsicScore);
			            
		            }
		            def controllerResScores = client.getControllerResponses()
			        updateScores(controllerResScores as List)
			        fineTuneNext(controllerResScores as List)

			        //Add to populationnnn
			        population[p3.getId()]=p3
		        }
		        catch (Exception ex)
		        {
		            println ex.toString()
		        }
		    }
		    //client.doLongEval(topXFromPopulation())
		    println 'iter '+(i*200)
		    sleep(120000);
		}
	}
	
	void fineTuneInit(Game g, double intrinsicScore)
	{
	    def resp = client.doShortEval(g,intrinsicScore,params.id)
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
	            def ft = fineTuning.remove(gameId) as Map
	            def numParams=ft.currentVersion.getNumParams()
	            //println 'finetuning game '+ft.currentVersion.getId()+' which has '+numParams+' params'
	            if (score <= ft.lastScore)
	            {
	                if ((ft.iters >params.shortFineTuneLimit && ft.lastScore<params.shortFineTuneThresh) ||
	                    ft.iters-ft.iterOfLastImprovement>params.fineTuneFamineLimit ||
	                    (ft.iters>=2 && numParams==1) ||
	                    ft.iters > ft.iterOfLastImprovement && ft.iterOfLastImprovement > params.fineTuneTotalLimit)
	                {
	                    if (ft.iters>2)//this is different enough
	                        population[ft.lastVersion.getId()]=ft.lastVersion
                        //println 'actually, done finetuning'
	                    continue;//we're done fine tuning this game
	                }
	                ft.currentVersion=ft.lastVersion.clone()
	                
	                def nextParam=ft.lastParam
	                def plus
	                if (ft.tried=='-')
	                {
	                    plus=true
	                    
	                }
	                else if (ft.tried=='+')
	                {
	                    plus=false
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
	                //ft.paramMap[nextParam]=plus
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
                    println "Worse: I'm going to tweak param "+nextParam+" up:"+plus
	            }
	            else
	            {
	                ft.iterOfLastImprovement=ft.iters
	                //population.add(ft.currentVersion) moved to ending
	                ft.lastVersions=ft.currentVersion.clone()
	                ft.lastScore=score
	                
	                if (ft.tried.length>0)
	                    ft.paramMap[ft.lastParam]=ft.tried[-1]
	                
                    //select a param
                    def nextParam=RANDOM.nextInt(numParams)
                    def plus
                    if (ft.paramMap.containsKey(nextParam) && ft.paramMap[nextParam]!=null)
                    {
                        plus=ft.paramMap[nextParam]=='+'
                    }
                    else
                    {
                        plus = (RANDOM.nextInt(2)==0)
                    }
                    
	                
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
                    println "Better: I'm going to tweak param "+nextParam+" up:"+plus
	            }
	            
	            //
	            ft.iters++
	            ft.currentVersion.setId(ft.origId+'_#'+ft.iters+'#')
	            client.doShortEval(ft.currentVersion,ft.lastScore,params.id)
	            fineTuning[ft.currentVersion.getId()]=ft
	            //
	        }
        }
	}

	//Helper Methods
	void printPopulationMembers()
	{
		for (String id : population.keySet())
		{
			println population[id].toString()
			println "\n"
		}
	}

	private Evolvable getRandomMember()
	{
		//Use stochastic universal sampling to select fitter individuals more often
		for (int i=0; i<1000; i++)
		{
		    double lowerBound = minScore + (maxScore - minScore) * RANDOM.nextDouble();
		    for (int j=0; j<10000; j++)
		    {
		        String id = this.randomPopulationID
		        if (population[id].getScore()>=lowerBound)
		            return population[id];
		    }
		    
		    
	    }
	    println 'ERROR, unable to randomly sample from population'
	    return population.iterator().next() as Evolvable;
	}


	private def getRandomPopulationID()
	{
		int item = RANDOM.nextInt(population.size())
		int i = 0
		for (def id : population.keySet())
		{
			if (i == item)
				return id
			i++
		}
	}
	
	
	private void updateScores(List toUpdate)
	{
	    
	    for (def i=0; i<toUpdate.size(); i++)
	    {
	        def gameId = toUpdate[i].id
	        def score = toUpdate[i].score.evalScore
	        population[gameId].setScore(score)
	        if (score>maxScore)
	            maxScore=score
            if (score<minScore)
                minScore=score	            
	        //You can access the elements of the score (for changing search strat) in the ${it}.score object
	        //also ${it}.score.id holds the identifier for the parameters used to create this score
	        
	        
	        //TODO, something with these
	        
	        //debugging
	        println 'Got score '+score+' for game '+gameId
	        //cont=false
	        
	        
	    }
	}
}

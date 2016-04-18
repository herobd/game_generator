package generator

//import game.Game;
import org.ggp.base.validator.ValidatorException
import org.ggp.base.validator.ValidatorWarning
import org.ggp.base.validator.StaticValidator
import org.ggp.base.validator.BasesInputsValidator
import org.ggp.base.validator.OPNFValidator
import org.ggp.base.validator.SimulationValidator


/**
 * @author Brian Davis
 *
 * A class for doign the instrinsic evaluation of a game's HAGDL and GDL
 */
class InstrinsicEvaluator 
{
    
    private Object params

	/**
	 *
	 * 
	 */
    InstrinsicEvaluator(Object params)
    {
        this.params=params
    }
	
    double evaluate(game.Game game)
    {
        StaticValidator v = new StaticValidator()
        BasesInputsValidator v2 = new BasesInputsValidator(2000)
        SimulationValidator v4 = new SimulationValidator(100,4)
        def invalid=0.0
        def gdl = game.convertToGDL().toString();
        org.ggp.base.util.game.Game theGame = org.ggp.base.util.game.Game.createEphemeralGame(org.ggp.base.util.game.Game.preprocessRulesheet(gdl));
        try
        {
            v.checkValidity_string(gdl)
            v2.checkValidity(theGame)
            v4.checkValidity(theGame)
            //println game.getId()+' is valid!'
        }
        catch (ValidatorException e)
        {
            //e.printStackTrace();
            invalid=1.0
            //println game.getId()+' is not valid :('
        }
        
        double numPlayers
        if (game.getNumPlayers()<2)
            numPlayers=0
        else
            numPlayers=1.0 - (game.getNumPlayers()-params.idealNumPlayers).abs()*params.numPlayersDrop
        

        def complexity = Math.min(1.0,Math.log10(game.complexityCount_piece()+4*game.complexityCount_end()+1) / 2.0)
        
        //TODO What portion of actions have random in postcondition
        //TODO What portion have interaction with other players pieces
        //For both of these, weight nested things less
        def combScore = params.invalidWeight*invalid + 
                        params.numPlayersWeight*numPlayers +
                        params.complexityWeight*complexity;
                        
        //println 'numPlayers: '+numPlayers+' complexity:'+complexity
        //println 'combScore: '+combScore
        game.setScore(combScore)
        return combScore
    }
	
	
}

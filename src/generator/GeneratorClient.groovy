package generator

import game.constructs.condition.Conditional
import game.constructs.player.Players
import game.constructs.TurnOrder
import game.constructs.board.Board
import game.constructs.condition.TerminalConditional
import game.constructs.end.EndGameConditions
import game.constructs.pieces.NamedPieces
import game.constructs.pieces.Piece
import game.gdl.clauses.GDLClause
import game.gdl.GDLConvertable
import game.gdl.GDLDescription
import generator.Evolvable
import generator.Gene
import game.Game
import groovy.json.JsonOutput

@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )


import groovyx.net.http.HTTPBuilder
import groovyx.net.http.ContentType
import groovyx.net.http.Method

/**
 * @author Brian Davis
 *
 * A class holding http communication functions
 */
class GeneratorClient 
{
    private String controllerAddress
    private Boolean connected
    private HTTPBuilder http
    private List<Object> backlog = []

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
	GeneratorClient(String controllerAddress)
	{
	    this.controllerAddress=controllerAddress;
        
        def status=''
        def tries=36 //try for an hour
        while(true)
        {
            try
            {
                println "connecting to http://"+controllerAddress
	            http = new HTTPBuilder("http://"+controllerAddress)
	            http.request(Method.GET,ContentType.JSON) {
	                uri.path="/connect"
	                uri.query= [ id:'generator', address: 'blocked' ]
	                response.success = { resp, json ->
                        status=json.status
                    }
                }
                
                if (status!="ok" && tries>0)
                {
                    println 'Failed to connect to Controller: '+status
                    tries--
                    sleep(10000) //10 seconds
                }
                else
                    break
            }
            catch (org.apache.http.NoHttpResponseException | org.apache.http.conn.HttpHostConnectException e)
            {
                println 'Failed to get connect Controller: '+e.toString()
                tries--
                sleep(20000) //20 seconds
            }
        }
        
        assert status=='ok'
	}
	
	String doShortEval(Game game, double intrinsicScore, int paramId)
	{
	    def meta = [    id:game.getId(), 
	                    name:game.getName(),
	                    intrinsicScore:intrinsicScore,
	                    generatorParamId:paramId, 
                        numPlayers:game.getNumPlayers(), 
                        gdlVersion:game.getGDLVersion(),
                        testLength:'short'
                    ]
        def content = [ meta:meta,
                        gdl:game.convertToGDL().toString(),
                        hlgdl:game.toString()
                      ]
        def status=''
        def tries=2
        while(true)
        {               
	        http.request(Method.POST,ContentType.JSON) {
	            uri.path="/submit_game"
	            body=JsonOutput.toJson(content)
	            response.success = { resp, json ->
                    status=json.status
                }
            }
            
            if (status!="ok" && tries>0)
            {
                println 'Failed to send game '+game.getId()+' to Controller: '+status
                tries--
                sleep(500) //.5 seconds
            }
            else
                break
        }
        if (status!='ok') {
            backlog.push([path:"/submit_game", body:JsonOutput.toJson(content)]);
        }
        
        return status
	}
	
	Object getControllerResponses()
	{
        def status=''
        def tries=2
        List ret=[]
        while(true)
        {   
            try
            {            
	            http.request(Method.GET,ContentType.JSON) {
	                uri.path="/asking_for_updates"
	                uri.query= [ id:'generator' ]
	                response.success = { resp, json ->
                        status=json.status
                        ret=json.scores
                    }
                }
                
                if (status!="ok" && tries>0)
                {
                    println 'Failed to get update from Controller: '+status
                    tries--
                    sleep(500) //.5 seconds
                }
                else
                    break
            }
            catch (org.apache.http.NoHttpResponseException | org.apache.http.conn.HttpHostConnectException e)
            {
                println 'Failed to get response from Controller: '+e.toString()
                tries--
                sleep(20000) //20 seconds
            }
        }
        
        assert ret instanceof List
        assert ret.size()==0 || (ret[0].id!=null)
        
        //out the back log
        while (status=='ok' && backlog.size()>0) {
            def toSend=backlog.pop()
            http.request(Method.POST,ContentType.JSON) {
	            uri.path=toSend.path
	            body=toSend.body
	            response.success = { resp, json ->
                    status=json.status
                }
            }
        }
        
        return ret
	}
	
	Object getLastParams()
	{
        def status=''
        def tries=4
        def ret
        while(true)
        {               
	        http.request(Method.GET,ContentType.JSON) {
	            uri.path="/lastParams"
	            uri.query= [ id:'generator' ]
	            response.success = { resp, json ->
                    status=json.status
                    ret=json.params
                }
            }
            
            if (status!="ok" && tries>0)
            {
                println 'Failed to get params from Controller: '+status
                tries--
                sleep(500) //.5 seconds
            }
            else
                break
        }
        
        
        
        return ret
	}
	
	
}

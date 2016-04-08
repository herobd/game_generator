#!/bin/env node
var express = require('express');
var fs      = require('fs');
var request = require('request');

var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer();



var learnPositionStrength = require('./strengthEvalLearn')();
/**
 *  Define the sample application.
 */
var EvaluatorServer = function(host,port) {

    //  Scope.
    var self = this;
    self.port=port;
    self.host=host;
    self.myAddress=host+':'+port;

    /*  ================================================================  */
    /*  Helper functions.                                                 */
    /*  ================================================================  */

    /**
     *  Populate the cache.
     */
    self.populateCache = function() {
        if (typeof self.zcache === "undefined") {
            self.zcache = { 'xx.html': '' };
        }

        //  Local cache for static content.
        //self.zcache['index.html'] = fs.readFileSync('./index.html');
        //self.zcache['projects.html'] = fs.readFileSync('./projects.html');
        
        //self.zcache['game/assests/Monster Growl-SoundBible.com-344645592.mp3'] = fs.readFileSync('./public/game/assests/Monster Growl-SoundBible.com-344645592.mp3');
        //self.zcache['game/assests/Zombie Moan-SoundBible.com-565291980.wav#t=0.1']=fs.readFileSync('./public/game/assests/Zombie Moan-SoundBible.com-565291980.wav#t=0.1');
        //self.zcache['game/assests/Japanese Temple Bell Small-SoundBible.com-113624364.mp3']=fs.readFileSync('./public/game/assests/Japanese Temple Bell Small-SoundBible.com-113624364.mp3');
    };


    /**
     *  Retrieve entry (content) from cache.
     *  @param {string} key  Key identifying content to retrieve from cache.
     */
    self.cache_get = function(key) { return self.zcache[key]; };


    /**
     *  terminator === the termination handler
     *  Terminate server on receipt of the specified signal.
     *  @param {string} sig  Signal to terminate on.
     */
    self.terminator = function(sig){
        self.sendDisconnect(self.controllerAddress);
        if (typeof sig === "string") {
           console.log('%s: Received %s - terminating EvaluatorServer ...',
                       Date(Date.now()), sig);
           process.exit(1);
        }
        console.log('%s: Node server stopped.', Date(Date.now()) );
    };


    /**
     *  Setup termination handlers (for exit and a list of signals).
     */
    self.setupTerminationHandlers = function(){
        //  Process on exit and signals.
        process.on('exit', function() { self.terminator(); });

        // Removed 'SIGPIPE' from the list - bugz 852598.
        ['SIGHUP', 'SIGINT', 'SIGQUIT', 'SIGILL', 'SIGTRAP', 'SIGABRT',
         'SIGBUS', 'SIGFPE', 'SIGUSR1', 'SIGSEGV', 'SIGUSR2', 'SIGTERM'
        ].forEach(function(element, index, array) {
            process.on(element, function() { self.terminator(element); });
        });
    };


    /*  ================================================================  */
    /*  App server functions (main app logic here).                       */
    /*  ================================================================  */

    /**
     *  Create the routing table entries + handlers for the application.
     */
    self.createRoutes = function() {
        self.routes = { };


        self.routes['/'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('index.html') );
        };
        self.routes['/a-page'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('a-page.html') );
        };
        self.routes['/redir'] = function(req, res) {
            res.redirect('https://somewhere');
        };
        
        self.routes['/connect'] = function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            if (req.query.id === 'controller') {
                self.controllerAddress=req.query.address;
                res.send('{"id":"evaluator","status":"ok"}');
                console.log('Connected to controller, '+self.controllerAddress);
                self.requestLastParams();
            } else {
                res.send('{"id":"evaluator","status":"id not recognized"}');
            }
            
            
        };
        self.routes['/disconnect'] = function(req, res) {
            //console.log(req.query.id + ' is disconnecting.');
        
            res.setHeader('Content-Type', 'application/json');
            if (req.query.id === 'controller') {
                console.log('Controller disconnected: '+self.controllerAddress);
                self.controllerAddress=null;
                res.send('{"id":"evaluator","status":"ok"}');
                
            } else {
                res.send('{"id":"evaluator","status":"id not recognized"}');
            }
        };
        
        
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() {
        self.createRoutes();
        self.app = express();//.createServer();
        self.app.use(bodyParser.json()); // for parsing application/json
        self.app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

        //  Add handlers for the app (from the routes).
        for (var r in self.routes) {
            self.app.get(r, self.routes[r]);
        }
        
        self.app.post('/gameResults', upload.array(), function (req, res, next) {
            console.log('Recieved for game id: '+req.body.gameId+' match: '+req.body.matchId+'_'+req.body.playerOrder+'_'+req.body.rep+'.');
            //console.log(req.body.printout);
            var status = self.evalRes(req.body);
            res.setHeader('Content-Type', 'application/json');
            res.json({id:'evaluator',status:status});
        });
        
        self.app.post('/gameDone', upload.array(), function (req, res, next) {
            console.log('Recieved DONE for game id: '+req.body.id+'.');
            res.setHeader('Content-Type', 'application/json');
            res.json({id:'evaluator',status:'ok'});
            
            
            
            self.evalGame(req.body,function (err,score) {
                self.returnScore(req.body.id,score,err);
            });
            
        });
        
        // Static file (images, css, etc)
        self.app.use(express.static('public'));
    };


    /**
     *  Initializes the sample application.
     */
    self.initialize = function() {
        //self.setupVariables();
        self.populateCache();
        self.setupTerminationHandlers();

        // Create the express server and routes.
        self.initializeServer();
        
        
        self.controllerAddress=null;
        
        self.matches=[];
        
        //params
        //TODO load last params
        self.params = {
                        id:0,
                        skillDifWeight:0.3,
                        prefLength:60,
                        drawishWeight:-0.2,
                        luckWeight:-0.2,
                        durationWeight:0.4,
                        resilienceWeight:0.4,
                        completionWeight:0.4,
                        clusteringKCoef:0.4, //multiply avg num of turns to have that many clusters
                        strengthEvalDrawWeight:0.5 //should be < 1.0
                      };
    };


    /**
     *  Start the server (starts up the sample application).
     */
    self.start = function() {
        //  Start the app on the specific interface (and port).
        self.app.listen(self.port, /*self.ipaddress,*/ function() {
            console.log('%s: Node server started on :%d ...',
                        Date(Date.now() ), self.port);
        });
    };
    
    
    //////////////////////////////additional functions
    
    
    self.sendConnect = function (address) {
        if (address !== null) {
            request.get('http://'+address+'/connect?id=evaluator&address='+self.myAddress, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                //console.log(body) // Show the HTML for the Google homepage. 
                var resj=JSON.parse(body);
                if (resj.status!=='ok') {
                    console.log('Failed to connect to: '+address);
                } else if (resj.id==='controller') {
                    self.controllerAddress=address;
                    console.log('Connected to '+resj.id+' '+address);
                    self.requestLastParams();
                }
              }
            });
        }
    };
    self.sendDisconnect = function (address) {
        if (address !== null) {
            request.get('http://'+address+'/disconnect?id=evaluator', function (error, response, body) {
              if (!error && response.statusCode == 200) {
                console.log('Disconnected: '+body); 
                self.controllerAddress==null;
              }
            });
        }
    };
    
    self.requestLastParams = function () {
        if (self.controllerAddress !== null) {
            request.get('http://'+self.controllerAddress+'/lastParams?id=evaluator', function (error, response, body) {
              if (!error && response.statusCode == 200) {
                var resj=JSON.parse(body);
                if (resj.status!=='ok') {
                    console.log('Failed to get last params: '+resj.status);
                } else if (resj.id==='controller') {
                    self.params=resj.params;
                }
              }
            });
        } else {
            console.log('ERROR: could not get last params, not connected to controller');
        }
    };
    
    self.returnScore = function(gameId,score,status) {
        request.post(
            'http://'+self.controllerAddress+'/gameScored',
            { json: {gameId:gameId, score:score, status:status} },
            function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    //Do something with body?
                    if (body.status!=='recieved'&&body.status!=='ok') {
                        console.log('ERROR: score for game:'+gameId+' didnt stick in controller: '+body.status);
                    }
                }
            }
        );
    }
    
    ////////////////////match too long
    self.RE_outcome = /INFO\([0-9.:]+\): Game over! results: ([0-9. ]+)/;
    self.RE_numbers = /[0-9.]+/g;
    self.RE_outOfTime = /match too long/;
    self.RE_states = /INFO\([0-9.:]+\): current state:\((\([a-zA-Z0-9_ ]*\))*\)/g;
    self.RE_state =  /INFO\([0-9.:]+\): current state:\(((\([a-zA-Z0-9_ ]*\))*)\)/;
    
    
    self.evalRes = function(matchResults) {
        var err='ok';
        console.log('outcome raw:'+matchResults.printout.match(self.RE_outcome)[1]);
        var outcome = matchResults.printout.match(self.RE_outcome)[1].match(self.RE_numbers);
        var finished = true;
        if(matchResults.printout.match(self.RE_outOfTime))
        {
            console.log('Out of time marker found: '+matchResults.printout.match(self.RRE_outOfTime)[0]);
            finished=false;
        }
        //console.log(outcome);
        if (outcome.length !== matchResults.players.length) {
            err='ERROR: could not match scores ('+outcome.length+') to players ('+matchResults.players.length+') for match '+matchResults.matchId;
            console.log(err);
            return err
        }
        
        var turnsLines = matchResults.printout.match(self.RE_states);
        var turns = [];
        for (var s of turnsLines) {
            turns.push(s.match(self.RE_state)[1]);
        }
        for (var i=0; i<outcome.length; i++)
            outcome[i]= +outcome[i];
        console.log('outcome parsed:'+outcome.join(', '))
        
        //var stats = self.gameStats[matchResults.gameId];
        var matchInfo = {
                            outcome:outcome,
                            finished:finished,
                            players:matchResults.players,
                            turns:turns
                        };
        
        
        
        
        //TODO
        if (self.matches[matchResults.gameId]===undefined)
            self.matches[matchResults.gameId]=[];
        self.matches[matchResults.gameId].push(matchInfo);
        
        return err;
    }
    
    self.evalGame = function(gameMeta,callback) {
        var err='ok';
        var stats;// = self.gameStats[gameMeta.gameId];
        
        var totalMatchesPlayed = 0.0+self.matches[gameMeta.id].length;
        
        var total_weakVstrong=0;
        var wins_weakVstrong=0;
        var draws_weakVstrong=0;
        
        var drawsWeighted=0.0;
        var playsWeighted=0.0;
        
        var winsByRandom=0;
        
        var lengthDevSum=0;
        var totalFinished=0;
        
        var winsByPosition=new Array(gameMeta.numPlayers);
        for (var i=0; i<gameMeta.numPlayers; i++)
            winsByPosition[i]=0;
            
        var sumLeadAtT = new Array(100);
        var linearScoreAtT = new Array(100);
        for (var i=0; i<100; i++) {
            sumLeadAtT[i]=0;
            linearScoreAtT[i]=0;
        }
        
        var leadChangeSum=0.0;
        var permSum=0.0;
        var killerSum=0.0;
        
        learnPositionStrength(JSON.parse(gameMeta.hlgdl),self.matches[gameMeta.id],gameMeta.numPlayers,self.params, function () {
        
            for (var matchInfo of self.matches[gameMeta.id]) {
                var minSkill=1000; var maxSkill=-1000;
                var idMinSkill=[];   var idMaxSkill=[];
                
                var maxScore=-1000; var maxScore2nd=-1; var minScore=1000;
                var playersAtMaxScore = [];
                var winningIndexes = [];
                var playersAt2ndMaxScore = [];
                for (var pIdx=0; pIdx<matchInfo.players.length; pIdx++) {
                    var p = matchInfo.players[pIdx];
                    if (p.skill>=maxSkill) {
                        if (p.skill==maxSkill) {
                            idMaxSkill.push(p.id);
                        } else {
                            idMaxSkill = [p.id];
                            maxSkill=p.skill;
                        }
                    }
                    if (p.skill<=minSkill) {
                        if (p.skill==minSkill) {
                            idMinSkill.push(p.id);
                        } else {
                            idMinSkill = [p.id];
                            minSkill=p.skill;
                        }
                    }
                    
                    var pScore = +matchInfo.outcome[pIdx];
                    if (pScore>=maxScore) {
                        if (pScore==maxScore) {
                            playersAtMaxScore.push(matchInfo.players[pIdx]);
                            winningIndexes.push(pIdx);
                        } else {
                            playersAt2ndMaxScore=playersAtMaxScore;
                            maxScore2nd=maxScore;
                            
                            playersAtMaxScore=[matchInfo.players[pIdx]];
                            winningIndexes=[pIdx];
                            maxScore=pScore;
                        }
                    } else if (maxScore2nd==-1) {
                        maxScore2nd=pScore;
                        playersAt2ndMaxScore[matchInfo.players[pIdx]];
                    } else if (maxScore2nd==pScore) {
                        playersAt2ndMaxScore.push(matchInfo.players[pIdx]);
                    }   
                    if (pScore<minScore) 
                        minScore=pScore;
                }
                var skillDif=maxSkill-minSkill;
                var weight = (skillDif*self.params.skillDifWeight);
                
                //drawish
                var draw = playersAtMaxScore.length>1;
                if (draw) {
                    drawsWeighted+=1+weight;
                    playsWeighted+=1+weight;
                } else {
                    playsWeighted+=Math.max(1-weight,0);//demphasize non-draw games which were unbalanced
                }
                
                
                //luck
                if (skillDif>1 || (minSkill==0 && maxSkill>0)) {
                    if (draw) {
                        if (playersAt2ndMaxScore.length==0)//always true for 2 player games
                            draws_weakVstrong+=1;
                        else {
                            if (matchInfo.players.length<3) {
                                //err = 'WARNING: draw with 2nd place players in 2 player game';
                                console.log('WARNING: draw with 2nd place players in 2 player game');
                                //return err;
                            }
                            //In the event of a 3+ player game, a coalition could allow for a weak
                            //player to tie for first with a better player.
                            //We compare the avg firstplace and secondplace skills to account for this.
                            var sum=0;
                            for (var p of playersAtMaxScore)
                                sum += p.skill;
                            var sum2=0;
                            for (var p of playersAt2ndMaxScore)
                                sum2 += p.skill;
                            
                            var skillDif2ndPlace = (sum2/(1.0*playersAt2ndMaxScore.length)) - (sum/(1.0*playersAtMaxScore.length))
                            if (skillDif2ndPlace>0.5)
                                draws_weakVstrong+=1;
                        }
                    }
                    else if (playersAtMaxScore[0].skill==minSkill)
                        wins_weakVstrong+=1;
                    
                    total_weakVstrong+=1;
                }
                
                
                //resilience
                var winnerIsRandom=true;
                for (var player of playersAtMaxScore) {
                    if (player.skill!=0) {
                        winnerIsRandom=false;
                        break;
                    }
                }
                if (winnerIsRandom && maxSkill>0) {
                    winsByRandom+=1;
                }
                
                //duration
                lengthDevSum += Math.abs(self.params.prefLength - matchInfo.turns.length)/(1.0*self.params.prefLength);
                
                //completion
                if (matchInfo.finished)
                    totalFinished+=1;
                
                //favorPosition
                for (var i=0; i<gameMeta.numPlayers; i++)
                    if (+matchInfo.outcome[i] == maxScore)
                        winsByPosition[i]+=1;


                //dynamics
                
                console.log('A match, final:'+matchInfo.outcome.join(', '));
                
                //First, create the lead information.
                //As we have >2 player games, I define 3 ways of computing the lead.
                //  I generally the compute stats on all three and take the max
                
                var losingIndexes=[];
                for (var i=0; i<gameMeta.numPlayers; i++) {
                    if (winningIndexes.indexOf(i)==-1)
                        losingIndexes.push(i);
                }
                
                var leadAvg = []
                var leadMin = []
                var leadMax = []
                for (turn of matchInfo.turnsStrengthScored) {
                    
                    console.log('str: '+turn.strengthScored);
                    
                    var winPos=0.0;
                    var winPosMax=null;
                    var winPosMin=null;
                    for (var i of winningIndexes) {
                        winPos+=turn.strengthScored[i];
                        if (turn.strengthScored[i]>winPosMax || winPosMax==null)
                            winPosMax=turn.strengthScored[i];
                        if (turn.strengthScored[i]<winPosMin || winPosMin==null)
                            winPosMin=turn.strengthScored[i];
                    }
                    winPos/=winningIndexes.length;
                    var otherPos=0.0;
                    var otherPosMax=null;
                    var otherPosMin=null;
                    for (var i of losingIndexes) {
                        otherPos+=turn.strengthScored[i];
                        if (turn.strengthScored[i]>otherPosMax || otherPosMax==null)
                            otherPosMax=turn.strengthScored[i];
                        if (turn.strengthScored[i]<otherPosMin || otherPosMin==null)
                            otherPosMin=turn.strengthScored[i];
                    }
                    otherPos/=losingIndexes.length;
                    
                    leadAvg.push(winPos-otherPos);
                    
                    leadMax.push(winPosMax-otherPosMax);
                    leadMin.push(winPosMin-otherPosMin);
                }
                
                //uncertaintyLate
                for (var i=0; i<100; i++) {
                    var t=(i/99.0);
                    linearScoreAtT[i]+=maxScore*t;
                    var turn=(matchInfo.turnsStrengthScored.length-1)*t;
                    var str1=leadAvg[Math.floor(turn)];
                    var str2=leadAvg[Math.ceil(turn)];
                    var intrLeadAvg=(str2-str1)*(turn-Math.floor(turn)) + str1;
                    
                    str1=leadMax[Math.floor(turn)];
                    str2=leadMax[Math.ceil(turn)];
                    var intrLeadMax=(str2-str1)*(turn-Math.floor(turn)) + str1;
                    
                    str1=leadMin[Math.floor(turn)];
                    str2=leadMin[Math.ceil(turn)];
                    var intrLeadMin=(str2-str1)*(turn-Math.floor(turn)) + str1;
                    sumLeadAtT[i]+=Math.max(intrLeadAvg,intrLeadMax,intrLeadMin)/maxScore;
                    if (sumLeadAtT[i]!=sumLeadAtT[i] || linearScoreAtT[i]!=linearScoreAtT[i]) {
                        console.log('ERROR, NaN: i='+i+' turn='+turn+' maxScore:'+maxScore);
                        console.log(intrLeadAvg+' '+intrLeadMax+' '+intrLeadMin);
                        console.log('sumLeadAtT:'+sumLeadAtT[i]+' linearScoreAtT:'+linearScoreAtT[i]);
                        callback("ERROR: NaN");
                        return
                    }
                }
                
                //leadChange
                function countLeadChanges(lead) {
                    var lastLead=Math.sign(lead[0]);
                    var countChanges=0;
                    for (var turn=1; turn<lead.length; turn++) {
                        var thisLead=Math.sign(lead[i]);
                        if (thisLead!=0) {
                            if (thisLead!=lastLead) {
                                countChanges++;
                                //console.log('lead change '+lead[turn-1]+' '+lead[turn]);
                            }
                            lastLead=thisLead;
                        }
                    }
                    return countChanges;
                }
                leadChangeSum += Math.max(countLeadChanges(leadAvg),countLeadChanges(leadMax),countLeadChanges(leadMin))/(0.0+leadAvg.length);
                
                //permanance
                function perm(lead) {
                    var ret=0;
                    for (var turn=2; turn<lead.length; turn++) {
                        ret+=Math.abs((lead[turn]-lead[turn-1])-(lead[turn-1]-lead[turn-2]));
                    }
                    return ret/(lead.length-2.0);
                }
                permSum += Math.max(perm(leadAvg),perm(leadMin),perm(leadMin))/maxScore;
                
                //killer moves
                function getKiller(lead) {
                    var maxMove=0;
                    for (var turn=1; turn<lead.length; turn++) {
                        var gain = Math.abs(lead[turn-1]-lead[turn]);
                        if (gain>maxMove)
                            maxMove=gain;
                    }
                    return maxMove;
                }
                killerSum += Math.max(getKiller(leadAvg),getKiller(leadMax),getKiller(leadMin))/maxScore;
                
            }//end match evals
            
            var positionWinsMost=null;
            var positionWinsLeast=null;
            for (var wins of winsByPosition) {
                if (wins>positionWinsMost || positionWinsMost===null)
                    positionWinsMost=wins;
                if (wins<positionWinsLeast || positionWinsLeast===null)
                    positionWinsLeast=wins;
            }
            
            
            
            //Caclulate scores
            var retScore={};
            
            retScore.drawish=(drawsWeighted)/(playsWeighted);
            
            if (total_weakVstrong!=0)
                retScore.luck=(wins_weakVstrong+draws_weakVstrong)/total_weakVstrong;
            else
                retScore.luck=0;
            
            retScore.duration=lengthDevSum/totalMatchesPlayed;
            
            retScore.resilience=(totalMatchesPlayed-winsByRandom)/totalMatchesPlayed;
            
            retScore.completion=totalFinished/totalMatchesPlayed;
            
            retScore.favorsPosition=(positionWinsMost-positionWinsLeast+0.0)/totalMatchesPlayed;
            
            var uncertainty=0;
            for (var i=0; i<100; i++) {
                uncertainty+=(linearScoreAtT[i]-sumLeadAtT[i])/totalMatchesPlayed;
            }
            retScore.uncertaintyLate=uncertainty/100.0;
            
            retScore.leadChange=leadChangeSum/totalMatchesPlayed;
            retScore.permanance=permSum/totalMatchesPlayed;
            retScore.killMoves=killerSum/totalMatchesPlayed;
            
            retScore.evalScore= self.params.drawishWeight * retScore.drawish +
                                self.params.luckWeight * retScore.luck +
                                self.params.durationWeight * retScore.duration +
                                self.params.resilienceWeight * retScore.resilience +
                                self.params.completionWeight * retScore.completion +
                                self.params.favorsPositionWeight * retScore.favorsPosition +
                                self.params.uncertaintyLateWeight * retScore.uncertaintyLate +
                                self.params.leadChangeWeight * retScore.leadChange + 
                                self.params.permananceWeight * retScore.permanance +
                                self.params.killMovesWeight * retScore.killMoves;
            
            retScore.id = self.params.id;
            
            console.log('Evaluation complete for game: '+gameMeta.id);
            console.log('  Drawish: '+retScore.drawish);
            console.log('  Luck: '+retScore.luck);
            console.log('  Duration: '+retScore.duration);
            console.log('  Resilience: '+retScore.resilience);
            console.log('  Completion: '+retScore.completion);
            console.log('  favorsPosition: '+retScore.favorsPosition);
            console.log('  uncertaintyLate: '+retScore.uncertaintyLate);
            console.log('  leadChange: '+retScore.leadChange);
            console.log('  permanance: '+retScore.permanance);
            console.log('  killMoves: '+retScore.killMoves);
            console.log('Combined: '+retScore.evalScore);
            //TODO more
            callback(err,retScore);
        });
    }
    
    
};   /*  END EvaluatorServer.  */



/**
 *  main():  Main code.
 */
var zapp = new EvaluatorServer(process.argv[2],+process.argv[3]);
zapp.initialize();
zapp.start();


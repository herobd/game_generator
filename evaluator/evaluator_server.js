#!/bin/env node
var express = require('express');
var fs      = require('fs');
var request = require('request');

var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer();


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
            console.log('Recieved match: '+req.body.matchId+'_'+req.body.playerOrder+'_'+req.body.rep+'. Stubed out');
            //console.log(req.body.printout);
            var status = self.evalRes(req.body);
            res.setHeader('Content-Type', 'application/json');
            res.json({id:'evaluator',status:status});
        });
        
        self.app.post('/gameDone', upload.array(), function (req, res, next) {
            console.log('Recieved DONE for game id: '+req.body.gameId+'. Stubed out');
            var score = {};
            var status = self.evalGame(req.body,score);
            res.setHeader('Content-Type', 'application/json');
            res.json({id:'evaluator',status:'recieved',score:score});
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
        
        //params
        //TODO load last params
        self.params = {
                        skillDifWeight:0.3
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
              }
            });
        }
    };
    
    ////////////////////
    self.RE_outcome = /^INFO\([0-9.:]+\): Game over! results: ([0-9. ]+)$/;
    self.RE_numbers = /[0-9.]/g;
    self.RE_state = /^INFO\([0-9.:]+\): current state:\((\([a-zA-Z0-9_ ]*\))*\)$/;
    self.evalRes = function(matchResults) {
        var err='ok';
        var outcome = matchResults.printout.match(self.RE_outcome)[1].match(self.RE_numbers);
        if (outcome.length !=== matchResults.players.length) {
            err='ERROR: could not match scores to players (length) for match '+matchResults.matchId;
            console.log(err);
            return err
        }
        
        //var stats = self.gameStats[matchResults.gameId];
        var matchInfo = {
                            outcome:outcome
                        };
        
        
        
        
        //TODO
        
        self.matches[matchResults.gameId]=matchInfo;
        
        return err;
    }
    self.evalGame = function(gameMeta,retScore) {
        var err='ok';
        var stats;// = self.gameStats[gameMeta.gameId];
        
        var drawsWeighted=0.0;
        var playsWeighted=0.0;
        for (var matchInfo of self.matches[gameMeta.gameId]) {
            var minSkill=1000; var maxSkill=-1000;
            var idMinSkill=[];   var idMaxSkill=[];
            
            var maxScore=-1000; var maxScore2nd=-1; var minScore=1000;
            var playersAtMaxScore = [];
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
                    if (p.skill==momSkill) {
                        idMinSkill.push(p.id);
                    } else {
                        idMinSkill = [p.id];
                        minSkill=p.skill;
                    }
                }
                
                var pScore = matchInfo.outcome[pIdx];
                if (pScore>=maxScore) {
                    if (pScore==maxScore)
                        playersAtMaxScore.push(matchResults.players[i]);
                    else {
                        playersAt2ndMaxScore=playersAtMaxScore;
                        maxScore2nd=maxScore;
                        
                        playersAtMaxScore=[matchResults.players[i]];
                        maxScore=pScore;
                    }
                } else if (maxScore2nd==-1) {
                    maxScore2nd=pScore;
                    playersAt2ndMaxScore[matchResults.players[i]];
                } else if (maxScore2nd==pScore) {
                    playersAt2ndMaxScore.push(matchResults.players[i]);
                }   
                if (pScore<minScore) 
                    minScore=pScore;
            }
            var skillDif=maxSkill-minSkill;
            var weight = 1 + (skillDif*self.params.skillDifWeight);
            
            var draw = playersAtMaxScore.length>1;
            if (draw) {
                drawsWeighted+=weight;
                playsWeighted+=weight;
            } else {
                playsWeighted+=1;//don't emphasize non-draw games
            }
            
            
            if (skillDif>1 || (minSkill==0 && maxSkill>0)) {
                if (draw) {
                    if (playersAt2ndMaxScore.length==0)//always true for 2 player games
                        draws_weakVstrong+=1;
                    else {
                        if (matchInfo.players.length<3) {
                            err = 'ERROR: draw with 2nd place players in 2 player game';
                            console.log(err);
                            return err;
                        }
                        //In the event of a 3+ player game, a coalition could allow for a weak
                        //player to tie for first with a better player.
                        //We compare the avg firstplace and secondplace skills to account for this.
                        var sum=0;
                        for (var p : playersAtMaxScore)
                            sum += p.skill;
                        var sum2=0;
                        for (var p : playersAt2ndMaxScore)
                            sum2 += p.skill;
                        
                        var skillDif2ndPlace = (sum2/(1.0*playersAt2ndMaxScore.length)) - (sum/(1.0*playersAtMaxScore.length))
                        if ( || skillDif2ndPlace>0.5)
                            draws_weakVstrong+=1;
                    }
                }
                else if (playersAtMaxScore[0].skill==minSkill)
                    wins_weakVstrong+=1;
                
                total_weakVstrong+=1;
            }
            
            
        }//end match evals
        //1, not drawish ------ 0, very drawish x
        retScore.decisive=(playsWeighted-drawsWeighted)/(playsWeighted);
        
        retScore.luck=(wins_weakVstrong+draws_weakVstrong)/total_weakVstrong;
        //TODO
        return err;
    }
};   /*  END EvaluatorServer.  */



/**
 *  main():  Main code.
 */
var zapp = new EvaluatorServer(process.argv[2],+process.argv[3]);
zapp.initialize();
zapp.start();


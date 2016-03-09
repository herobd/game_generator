#!/bin/env node

/*Brian Davis
 *Controller for CS673 game creator project
 */

//curl -d 'meta={"id":"0","score":1.0,"testLength":"s","name":"ttt","numPlayers":2}' -d gdl=sssss -d hlgdl=sdfdd localhost:8080/submit_game

var express = require('express');
var fs      = require('fs');
var request = require('request');

var bodyParser = require('body-parser');
var multer = require('multer'); // v1.0.5
var upload = multer();



var Database = require('./database')();
var GameCord = require('./gameCord')();

/**
 *  Define the sample application.
 */
var ControllerApp = function(host,port) {

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
        
        if (typeof sig === "string") {
           console.log('%s: Received %s - terminating control app ...',
                       Date(Date.now()), sig);
           self.sendDisconnect(self.evaluatorAddress,function() {
             self.sendDisconnect(self.generatorAddress, function() {
               process.exit(1); 
             });
           });
           
           //process.exit(1);
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


        /*self.routes['/'] = function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('index.html') );
        };*/
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
            if (req.query.id === 'generator') {
                self.generatorAddress=req.query.address;
                res.send('{"id":"controller","status":"ok"}');
                console.log('Connected to generator, '+self.generatorAddress);
            } else if (req.query.id === 'evaluator') {
                self.evaluatorAddress=req.query.address;
                res.send('{"id":"controller","status":"ok"}');
                console.log('Connected to evaluator, '+self.evaluatorAddress);
            } else {
                res.send('{"id":"controller","status":"id not recognized"}');
            }
            
            
        };
        self.routes['/disconnect'] = function(req, res) {
            //console.log(req.query.id + ' is disconnecting.');
        
            res.setHeader('Content-Type', 'application/json');
            if (req.query.id === 'generator') {
                self.generatorAddress=null;
                res.send('{"id":"controller","status":"ok"}');
                console.log('Disconnected from generator, '+self.generatorAddress);
            } else if (req.query.id === 'evaluator') {
                self.evaluatorAddress=null;
                res.send('{"id":"controller","status":"ok"}');
                console.log('Disconnected from evaluator, '+self.evaluatorAddress);
            } else {
                res.send('{"id":"controller","status":"id not recognized"}');
            }
        };
        
        self.routes['/addPlayer'] = function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            var err = self.gameCord.addPlayer(req.query.method,req.query.host,+req.query.port,req.query.name,+req.query.skillLevel,+req.query.gdlVersion);
            res.send('{"id":"controller","status":"'+err+'","player":"'+req.query.name+'"}');
            
            
        };
        
        self.routes['/executeConnect'] = function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            var err = self.gameCord.addPlayer(req.query.method,req.query.host,+req.query.port,req.query.name,+req.query.skillLevel,+req.query.gdlVersion);
            self.sendConnect(req.query.address,function(err){res.send('{"id":"controller","status":"'+err+'"}');});
            
            
            
        };
        
        self.routes['/lastParams'] = function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            if (req.query.id === 'evaluator') {
                database.getLastParams(req.query.id,function(err,params) {
                    res.json({id:"controller",status:err, params:params});
                });
            } else {
                res.json({id:"controller",status:"id not recognized"});
            }
            
        };

        
        self.routes['/testSend'] = function(req, res) {
            self.sendGameResults({id:0, name:'test'});
            res.send('ok');
        }
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
        
        self.app.post('/submit_game', upload.array(), function (req, res, next) {
            console.log(req.body);
            meta = JSON.parse(req.body.meta);//meta has id and score atleast
            res.setHeader('Content-Type', 'application/json');
            if (meta.id && meta.score && req.body.gdl && req.body.hlgdl)
            {
                var err=self.gameCord.enqueue(meta);
                self.database.storeGame(meta,req.body.gdl,req.body.hlgdl);
                res.json({id:meta.id, status:err});
            }
            else
            {
                res.json({status:'malformed'});
            }
        });
        
        self.app.post('/gameScored', upload.array(), function (req, res, next) {
            
            res.setHeader('Content-Type', 'application/json');
            res.json({id:'evaluator',status:'ok'});
            if (req.body.status=='ok') {
                var err;
                self.database.storeScore(req.body.gameId,req.body.score,function(pe){err=pe;});
                if (err!='ok')
                    console.log(err);        
                err=self.sendGameScore(req.body.gameId,req.body.score);
                if (err!='ok')
                    console.log(err);
            }
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
        
        self.database=new Database('localhost:8888');
        if (self.database.test!=='ok')
            console.log('ERROR: database failed to init');
        self.gameCord=new GameCord(self,self.database);
        if (self.gameCord.allPlayerTypes[0]!=='random')
            console.log('ERROR: gameCord failed to init');
        self.evaluatorAddress=null;
        self.generatorAddress=null;
    };


    /**
     *  Start the server (starts up the sample application).
     */
    self.start = function() {
        //  Start the app on the specific interface (and port).
        self.app.listen(self.port, /*self.ipaddress,*/ function() {
            console.log('%s: Node server started on :%d ...',
                        Date(Date.now() ), self.port);
            
            //TODO DEBUG init////
            self.sendConnect('localhost:8081',function(err){console.log(err);});
            self.gameCord.addPlayer('heuristic','localhost',9148,'heu1',2,1);
            self.gameCord.addPlayer('minimax','localhost',9147,'min1',1,1);
            ////////////////////
        });
    };
    
    
    //////////////////////////////additional functions
    self.sendGameResults = function(results) {
        
        database.storeGameResults(results,function(err) {
            if (err!='ok') {
                console.log('DB failed to save game results, game:'+results.gameId+' match:'+results.matchId);
            }
        });
        request.post(
            'http://'+self.evaluatorAddress+'/gameResults',
            { json: results },
            function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    //Do something with body?
                    if (body.status!=='recieved'&&body.status!=='ok') {
                        console.log('ERROR: match '+results.id+' didnt stick in evaluator');
                    }
                }
            }
        );
    };
    
    self.sendGameDone = function(results) {
        
        request.post(
            'http://'+self.evaluatorAddress+'/gameDone',
            { json: results },
            function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    
                    if (body.status!=='recieved'&&body.status!=='ok') {
                        console.log('ERROR: match '+results.id+' didnt stick in evaluator');
                    }
                    
                }
            }
        );
    };
    
    //localhost:8081/connect?id=controller&address=localhost:8080
    //http://localhost:8081/connect?id=controller&address=localhost:8080
    
    self.sendConnect = function (address,callback) {
        if (address !== null) {
            var url = encodeURI('/connect?id=controller&address='+self.myAddress);
            //console.log('send ' + address+url);
            request.get('http://'+address+url, function (error, response, body) {
              if (!error && response.statusCode == 200) {
                //console.log(body) // Show the HTML for the Google homepage. 
                var resj=JSON.parse(body);
                if (resj.status!=='ok') {
                    console.log('Failed to connect to: '+address);
                    callback('failed:'+resj.status);
                } else {
                    console.log('Connected to '+resj.id+' '+address);
                    if (resj.id==='generator') {
                    self.generatorAddress=address;
                    }
                    else if (resj.id==='evaluator') {
                        self.evaluatorAddress=address;
                    }
                    //self.sendDisconnect(address);
                    callback('ok');
                }
              } else {
                if (error) {
                    console.log('get ERROR: '+error);
                    callback(error);
                } else {
                    console.log('get ERROR code:'+response.statusCode);
                    callback(response.statusCode);
                }
              }
            });
        }
    };
    self.sendDisconnect = function (address,callback) {
        if (address !== null) {
            //console.log('Sending disconnect '+'http://'+address+'/disconnect?id=controller');
            request.get('http://'+address+'/disconnect?id=controller', function (error, response, body) {
              if (!error && response.statusCode == 200) {
                console.log('Disconnected: '+body);
              } else {
                console.log('get ERROR: '+error+' code:'+response.statusCode);
              }
              callback();
            });
        } else {
            callback();
        }
    };
};   /*  END Controller Application.  */



/**
 *  main():  Main code.
 */
var zapp = new ControllerApp(process.argv[2],+process.argv[3]);
zapp.initialize();
zapp.start();


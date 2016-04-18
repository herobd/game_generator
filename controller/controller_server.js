#!/bin/env node

/*Brian Davis
 *Controller for CS673 game creator project
 */

//curl -d 'meta={"id":"0","intrinsicScore":1.0,"testLength":"s","name":"ttt","numPlayers":2}' -d gdl=sssss -d hlgdl=sdfdd localhost:8080/submit_game

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
        
    };


    /**
     *  Initialize the server (express) and create the routes and register
     *  the handlers.
     */
    self.initializeServer = function() {
        self.createRoutes();
        self.app = express();//.createServer();
        self.app.use(express.static('public'));
        self.app.use(bodyParser.json());
        
        //self.app.use(bodyParser.json()); // for parsing application/json
        //self.app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

        //  Add handlers for the app (from the routes).
        for (var r in self.routes) {
            self.app.get(r, self.routes[r]);
        }
        //GET
        self.app.get('/a-page', function(req, res) {
            res.setHeader('Content-Type', 'text/html');
            res.send(self.cache_get('a-page.html') );
        });
        self.app.get('/redir', function(req, res) {
            res.redirect('https://somewhere');
        });
        
        self.app.get('/connect', function(req, res) {
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
            
            
        });
        self.app.get('/disconnect', function(req, res) {
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
        });
        
        self.app.get('/addPlayer', function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            var err = self.gameCord.addPlayer(req.query.method,req.query.host,+req.query.port,req.query.name,+req.query.skillLevel,+req.query.gdlVersion);
            res.send('{"id":"controller","status":"'+err+'","player":"'+req.query.name+'"}');
            
            
        });
        
        self.app.get('/executeConnect', function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            var err = self.gameCord.addPlayer(req.query.method,req.query.host,+req.query.port,req.query.name,+req.query.skillLevel,+req.query.gdlVersion);
            self.sendConnect(req.query.address,function(err){res.send('{"id":"controller","status":"'+err+'"}');});
            
            
            
        });
        
        self.app.get('/lastParams', function(req, res) {
            //console.log(req.query.id + ' is connecting.');
            
            res.setHeader('Content-Type', 'application/json');
            if (req.query.id === 'evaluator' || req.query.id === 'generator') {
                self.database.getLastParams(req.query.id,function(err,params) {
                    res.json({id:"controller",status:err, params:params});
                });
            } else {
                res.json({id:"controller",status:"id not recognized"});
            }
            
        });
        
        self.app.get('/asking_for_updates', function(req, res) {
            
            
            res.setHeader('Content-Type', 'application/json');
            if (req.query.id === 'generator') {
                res.json({id:"controller",status:'ok', scores:self.returnToGenerator});
                self.returnToGenerator=[];
            } else {
                res.json({id:"controller",status:"id not recognized"});
            }
            
            
        });
        
        self.app.get('/status', function(req, res) {

            res.setHeader('Content-Type', 'application/json');
            res.json({queueLength:self.gameCord.queueLength(),
                      numMatchesBeingPlayed:self.gameCord.numMatchesBeingPlayed,
                      numPlayers:self.gameCord.numPlayers(),
                      numPlayerTypes:self.gameCord.numPlayerTypes(),
                      gameBeingEval1:self.gameCord.gameBeingEval1(),
                      gameBeingEval2:self.gameCord.gameBeingEval2(),
                      ranking:self.gameCord.rankMode});
        });
        
        self.app.get('/startRanking', function(req, res) {
            res.setHeader('Content-Type', 'application/json');
            self.gameCord.rankPlayersStart();
            res.json({id:"controller",status:"ok"});
        });
        
        self.app.get('/endRanking', function(req, res) {
            res.setHeader('Content-Type', 'application/json');
            var results = self.gameCord.rankPlayersEnd();
            res.json({id:"controller",status:"ok",results:results});
        });
        
        self.app.get('/getGames', function(req, res) {
            res.setHeader('Content-Type', 'application/json');
            self.database.getTopGames(+req.query.num,function(err,games) {
                console.log(games);
                if (!err)
                    res.json({id:"controller",status:"ok",games:games});
                else
                    res.json({id:"controller",status:err,games:null});
            });
        });

        
        self.app.get('/testSend', function(req, res) {
            self.sendGameResults({id:0, name:'test'});
            res.send('ok');
        });
        
        ///TEST///
    self.app.get('/testGame', function(req,res) {
        var meta = 
            {
                id:'test', 
                name:'battle',
                intrinsicScore:0.1,
                generatorParamId:0, 
                numPlayers:2, 
                gdlVersion:1,
                testLength:'short'
            };
        var game = {
                meta:meta,
                gdl: '; A synthetic game of survival.\n\n\
\n\n\
; Roles\n\n\
(role north)\n\n\
(role south)\n\
\n\
; Initial state\n\
(init (step 0))\n\
(init (capture_count north 0))\n\
(init (capture_count south 0))\n\
\n\
(init (cell 2 1 south_p))\n\
(init (cell 3 1 south_k))\n\
(init (cell 4 1 south_k))\n\
(init (cell 5 1 south_k))\n\
(init (cell 6 1 south_k))\n\
(init (cell 7 1 south_p))\n\
(init (cell 3 2 south_p))\n\
(init (cell 4 2 south_p))\n\
(init (cell 5 2 south_p))\n\
(init (cell 6 2 south_p))\n\
\n\
(init (cell 2 8 north_p))\n\
(init (cell 3 8 north_k))\n\
(init (cell 4 8 north_k))\n\
(init (cell 5 8 north_k))\n\
(init (cell 6 8 north_k))\n\
(init (cell 7 8 north_p))\n\
(init (cell 3 7 north_p))\n\
(init (cell 4 7 north_p))\n\
(init (cell 5 7 north_p))\n\
(init (cell 6 7 north_p))\n\
\n\
(init (cell 1 2 south_p))\n\
(init (cell 1 3 south_k))\n\
(init (cell 1 4 south_k))\n\
(init (cell 1 5 south_k))\n\
(init (cell 1 6 south_k))\n\
(init (cell 1 7 south_p))\n\
(init (cell 2 3 south_p))\n\
(init (cell 2 4 south_p))\n\
(init (cell 2 5 south_p))\n\
(init (cell 2 6 south_p))\n\
\n\
(init (cell 8 2 north_p))\n\
(init (cell 8 3 north_k))\n\
(init (cell 8 4 north_k))\n\
(init (cell 8 5 north_k))\n\
(init (cell 8 6 north_k))\n\
(init (cell 8 7 north_p))\n\
(init (cell 7 3 north_p))\n\
(init (cell 7 4 north_p))\n\
(init (cell 7 5 north_p))\n\
(init (cell 7 6 north_p))\n\
\n\
; Legal moves\n\
(<= (legal ?role (move ?x ?y ?u ?v))\n\
    (role ?role)\n\
    (coordinate ?u)\n\
    (coordinate ?v)\n\
    (owns ?role ?piece)\n\
    (true (cell ?x ?y ?piece))\n\
    (are_distinct ?x ?y ?u ?v)\n\
    (valid_move ?piece ?x ?y ?u ?v)\n\
    (not (cell_owned_by ?u ?v ?role)))\n\
\n\
(<= (cell_owned_by ?u ?v ?role)\n\
    (true (cell ?u ?v ?p))\n\
    (owns ?role ?p))\n\
\n\
(<= (legal ?role (defend ?x ?y))\n\
    (true (cell ?x ?y ?piece))\n\
    (owns ?role ?piece))\n\
\n\
; Transition rules\n\
(<= (next (cell ?x ?y ?piece))\n\
    (true (cell ?x ?y ?piece))\n\
    (not (someone_moves_from ?x ?y))\n\
    (not (someone_moves_to ?x ?y)))\n\
\n\
(<= (someone_moves_from ?x ?y)\n\
    (does ?r (move ?x ?y ?u ?v)))\n\
\n\
(<= (someone_moves_to ?u ?v)\n\
    (does ?r (move ?x ?y ?u ?v)))\n\
\n\
(<= (next (cell ?x ?y ?piece))\n\
    (true (cell ?x ?y ?piece))\n\
    (does ?role (defend ?x ?y)))\n\
(<= (next (cell ?x ?y ?piece))\n\
    (does ?role (move ?x0 ?y0 ?x ?y))\n\
    (true (cell ?x0 ?y0 ?piece))\n\
    (not (someone_defends ?x ?y))\n\
    (not (someone_different_moves_to ?role ?x ?y)))\n\
\n\
(<= (someone_defends ?x ?y)\n\
    (does ?owner (defend ?x ?y)))\n\
\n\
(<= (next (step ?b))\n\
    (true (step ?a))\n\
    (succ ?a ?b))\n\
(<= (next (capture_count ?role ?new_count))\n\
    (true (capture_count ?role ?old_count))\n\
    (did_capture ?role)\n\
    (succ ?old_count ?new_count))\n\
(<= (next (capture_count ?role ?count))\n\
    (true (capture_count ?role ?count))\n\
    (not (did_capture ?role)))\n\
\n\
; Termination\n\
(<= terminal\n\
    (true (step 30)))\n\
(<= terminal\n\
    (role ?role)\n\
    (not (any_cell_owned_by ?role)))\n\
\n\
(<= (any_cell_owned_by ?role)\n\
    (cell_owned_by ?x ?y ?role))\n\
\n\
(<= terminal\n\
    (role ?role)\n\
    (goal ?role 100))\n\
\n\
; Payoffs\n\
(<= (goal ?role ?payoff)\n\
    (true (capture_count ?role ?count))\n\
    (payoff ?count ?payoff))\n\
\n\
; Domain knowledge\n\
(owns south south_p)\n\
(owns north north_p)\n\
(owns west wp)\n\
(owns east ep)\n\
\n\
(owns south south_k)\n\
(owns north north_k)\n\
(owns west wk)\n\
(owns east ek)\n\
\n\
(pawn south_p)\n\
(pawn north_p)\n\
(pawn ep)\n\
(pawn wp)\n\
\n\
(king south_k)\n\
(king north_k)\n\
(king ek)\n\
(king wk)\n\
\n\
(payoff 0 0)\n\
(payoff 1 10)\n\
(payoff 2 20)\n\
(payoff 3 30)\n\
(payoff 4 40)\n\
(payoff 5 50)\n\
(payoff 6 60)\n\
(payoff 7 70)\n\
(payoff 8 80)\n\
(payoff 9 90)\n\
(payoff 10 100)\n\
\n\
; Valid moves\n\
; Kings can move vertically, horizontally, or diagonally.\n\
(<= (valid_move ?piece ?x ?y ?u ?v)\n\
    (king ?piece)\n\
    (within_one ?x ?u)\n\
    (within_one ?y ?v))\n\
; Pawns can move vertically or horizontally.\n\
(<= (valid_move ?piece ?x ?y ?u ?y)\n\
    (pawn ?piece)\n\
    (within_one ?x ?u)\n\
    (coordinate ?y))\n\
(<= (valid_move ?piece ?x ?y ?x ?v)\n\
    (pawn ?piece)\n\
    (coordinate ?x)\n\
    (within_one ?y ?v))\n\
\n\
(<= (within_one ?x ?x)\n\
    (number ?x))\n\
(<= (within_one ?x ?u)\n\
    (succ ?x ?u))\n\
(<= (within_one ?x ?u)\n\
    (succ ?u ?x))\n\
\n\
(<= (are_distinct ?x1 ?y1 ?x2 ?y2)\n\
    (coordinate ?x1)\n\
    (coordinate ?x2)\n\
    (coordinate ?y1)\n\
    (coordinate ?y2)\n\
    (distinct ?x1 ?x2))\n\
\n\
(<= (are_distinct ?x1 ?y1 ?x2 ?y2)\n\
    (coordinate ?x1)\n\
    (coordinate ?x2)\n\
    (coordinate ?y1)\n\
    (coordinate ?y2)\n\
    (distinct ?y1 ?y2))\n\
\n\
(<= (did_capture ?role)\n\
    (does ?role (move ?x ?y ?u ?v))\n\
    (true (cell ?u ?v ?piece))\n\
    (owns ?opponent ?piece)\n\
    (not (does ?opponent (defend ?u ?v)))\n\
    (not (move_from ?opponent ?u ?v))\n\
    (not (someone_different_moves_to ?role ?u ?v)))\n\
(<= (did_capture ?defender)\n\
    (does ?attacker (move ?x ?y ?u ?v))\n\
    (does ?defender (defend ?u ?v)))\n\
\n\
(<= (move_from ?role ?u ?v)\n\
    (does ?role (move ?u ?v ?u2 ?v2)))\n\
\n\
(<= (someone_different_moves_to ?role ?u ?v)\n\
    (role ?role)\n\
    (role ?op2)\n\
    (distinct ?role ?op2)\n\
    (does ?op2 (move ?xx ?yy ?u ?v)))\n\
\n\
(<= (sum ?x 0 ?x)\n\
    (number ?x))\n\
(<= (sum ?a 1 ?b)\n\
    (succ ?a ?b))\n\
(<= (sum ?a 2 ?c)\n\
    (succ ?a ?b)\n\
    (succ ?b ?c))\n\
(<= (sum ?a 3 ?d)\n\
    (succ ?a ?b)\n\
    (succ ?b ?c)\n\
    (succ ?c ?d))\n\
\n\
(coordinate 1)\n\
(coordinate 2)\n\
(coordinate 3)\n\
(coordinate 4)\n\
(coordinate 5)\n\
(coordinate 6)\n\
(coordinate 7)\n\
(coordinate 8)\n\
\n\
(succ 0 1)\n\
(succ 1 2)\n\
(succ 2 3)\n\
(succ 3 4)\n\
(succ 4 5)\n\
(succ 5 6)\n\
(succ 6 7)\n\
(succ 7 8)\n\
(succ 8 9)\n\
(succ 9 10)\n\
(succ 10 11)\n\
(succ 11 12)\n\
(succ 12 13)\n\
(succ 13 14)\n\
(succ 14 15)\n\
(succ 15 16)\n\
(succ 16 17)\n\
(succ 17 18)\n\
(succ 18 19)\n\
(succ 19 20)\n\
(succ 20 21)\n\
(succ 21 22)\n\
(succ 22 23)\n\
(succ 23 24)\n\
(succ 24 25)\n\
(succ 25 26)\n\
(succ 26 27)\n\
(succ 27 28)\n\
(succ 28 29)\n\
(succ 29 30)\n\
(succ 30 31)\n\
(succ 31 32)\n\
(succ 32 33)\n\
(succ 33 34)\n\
(succ 34 35)\n\
(succ 35 36)\n\
(succ 36 37)\n\
(succ 37 38)\n\
(succ 38 39)\n\
(succ 39 40)\n\
(succ 40 41)\n\
(succ 41 42)\n\
(succ 42 43)\n\
(succ 43 44)\n\
(succ 44 45)\n\
(succ 45 46)\n\
(succ 46 47)\n\
(succ 47 48)\n\
(succ 48 49)\n\
(succ 49 50)\n\
\n\
(number 0)\n\
(number 1)\n\
(number 2)\n\
(number 3)\n\
(number 4)\n\
(number 5)\n\
(number 6)\n\
(number 7)\n\
(number 8)\n\
(number 9)\n\
(number 10)\n\
(number 11)\n\
(number 12)\n\
(number 13)\n\
(number 14)\n\
(number 15)\n\
(number 16)\n\
(number 17)\n\
(number 18)\n\
(number 19)\n\
(number 20)\n\
(number 21)\n\
(number 22)\n\
(number 23)\n\
(number 24)\n\
(number 25)\n\
(number 26)\n\
(number 27)\n\
(number 28)\n\
(number 29)\n\
(number 30)\n\
(number 31)\n\
(number 32)\n\
(number 33)\n\
(number 34)\n\
(number 35)\n\
(number 36)\n\
(number 37)\n\
(number 38)\n\
(number 39)\n\
(number 40)\n\
(number 41)\n\
(number 42)\n\
(number 43)\n\
(number 44)\n\
(number 45)\n\
(number 46)\n\
(number 47)\n\
(number 48)\n\
(number 49)\n\
(number 50)',
                hlgdl: '{\n"players": ["north","south"],\n"board": {\n"macroType":"Grid",\n"tileType":"Square",\n"layoutShape":"Square",\n"size":8\n},\n"pieces":[{"name":"p"},{"name":"k"}]\n}'
            };
        self.database.storeGame(game.meta,game.gdl,game.hlgdl, function(err) {
            var err=self.gameCord.enqueue(game.meta);
        
            res.json({id:game.meta.id, status:err});
        });
    });
    //END TEST///
        
        
        
        //POST
        
        self.app.post('/submit_game',/* upload.array(),*/ function (req, res, next) {
            //console.log(req.body.meta);
            //meta = JSON.parse(req.body.meta);//meta has id and score atleast
            res.setHeader('Content-Type', 'application/json');
            if (req.body.meta.id && req.body.meta.intrinsicScore && req.body.gdl && req.body.hlgdl)
            {
                self.database.storeGame(req.body.meta,req.body.gdl,req.body.hlgdl, function(err) {
                    var err=self.gameCord.enqueue(req.body.meta);
                
                    res.json({id:req.body.meta.id, status:err});
                });
                
            }
            else
            {
                res.json({status:'malformed'});
                
            }
            //console.log(req.body);
        });
        
        self.app.post('/gameScored', upload.array(), function (req, res, next) {
            
            res.setHeader('Content-Type', 'application/json');
            res.json({id:'evaluator',status:'ok'});
            if (req.body.status=='ok') {
                self.returnToGenerator.push({id:req.body.gameId,score:req.body.score});
                self.database.storeScore(req.body.gameId,req.body.score,function(err){console.log(err);});
                    
            }
        });
        
        
        
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
        
        
        
        self.evaluatorAddress=null;
        self.generatorAddress=null;
        self.returnToGenerator=[];
        
    };


    /**
     *  Start the server (starts up the sample application).
     */
    self.start = function() {
        //  Start the app on the specific interface (and port).
        self.database=new Database('localhost:27017', './gdl',function() {
            self.gameCord=new GameCord(self,self.database);
            
            self.app.listen(self.port, /*self.ipaddress,*/ function() {
                console.log('%s: Node server started on :%d ...',
                            Date(Date.now() ), self.port);
                
                //TODO init////
                self.sendConnect('localhost:8081',function(err){console.log(err);});
                
                //self.gameCord.addPlayer('minimax','localhost',9147,'min1',1,1);
                //self.gameCord.addPlayer('heuristic','localhost',9148,'heu1',2,1);
                /*self.gameCord.addPlayer('l1','localhost',9147,'l1',0,1);
                self.gameCord.addPlayer('l2','localhost',9148,'l2',0,1);
                self.gameCord.addPlayer('l3','localhost',9149,'l3',0,1);
                self.gameCord.addPlayer('l4','localhost',9150,'l4',0,1);
                self.gameCord.addPlayer('l1','localhost',9151,'l5',0,1);
                self.gameCord.addPlayer('l2','localhost',9152,'l6',0,1);
                self.gameCord.addPlayer('l3','localhost',9153,'l7',0,1);
                self.gameCord.addPlayer('l4','localhost',9154,'l8',0,1);*/
                //self.gameCord.addPlayer('l','localhost',9147,'l',0,1);
                
                //self.gameCord.addPlayer('montecarlo','normandy',9147,'m');
                //self.gameCord.addPlayer('montecarlotreesearch','normandy',9148,'mt');
                
                //self.gameCord.addPlayer('heurisitc_prune','normandy',9150,'h_prune');
                //self.gameCord.addPlayer('heurisitc','normandy',9149,'h');
                //self.gameCord.addPlayer('heurisitc_rand','localhost',9147,'h_r');
                //self.gameCord.addPlayer('heurisitc','localhost',9148,'h');
                
                //THESE ARE RUNNING
                /*self.gameCord.addPlayer('PAST','normandy',59834,'past');
                self.gameCord.addPlayer('UCT','normandy',59835,'uct');
                self.gameCord.addPlayer('MAST','normandy',59836,'mast');
                self.gameCord.addPlayer('TO-MAST','normandy',59837,'to-mast');
                
                self.gameCord.addPlayer('PAST','normandy',59838,'past2');
                self.gameCord.addPlayer('UCT','normandy',59839,'uct2');
                self.gameCord.addPlayer('MAST','normandy',59840,'mast2');
                self.gameCord.addPlayer('TO-MAST','normandy',59841,'to-mast2');*/
                
                //FOR LOCAL
                self.gameCord.addPlayer('montecarlo','localhost',9147,'m');
                self.gameCord.addPlayer('montecarlo','localhost',9148,'m2');
                
                ////////////////////
                
                self.database.getAllUnscoredGames(function(err,unscoredGames) {
                    for (game of unscoredGames) {
                        if (game.startedEval==false)
                            self.gameCord.enqueue(game.meta);
                        else {
                            self.gameCord.enqueuePartail(game.meta,game.matches);
                            //TODO retrieve all match results and pass to evaluator
                        }
                    }
                });
                
                
            });
        });
    };
    
    
    
    
    //////////////////////////////additional functions
    self.sendGameResults = function(results) {
        
        self.database.storeGameResults(results,function(err) {
            if (err!='ok') {
                console.log('DB failed to save game results, game:'+results.gameId+' match:'+results.id+', err:'+err);
            }
        });
        request.post(
            'http://'+self.evaluatorAddress+'/gameResults',
            { json: results },
            function (error, response, body) {
                if (!error && response.statusCode == 200) {
                    //Do something with body?
                    if (body.status!=='recieved'&&body.status!=='ok') {
                        console.log('ERROR: match '+results.id+' didnt stick in evaluator: '+body.status);
                    }
                }
            }
        );
    };
    
    self.sendGameDone = function(gameId) {
        self.database.getMetaForEval(gameId, function(err,res) {
            if (!err) {
                request.post(
                    'http://'+self.evaluatorAddress+'/gameDone',
                    { json: res },
                    function (error, response, body) {
                        if (!error && response.statusCode == 200) {
                            
                            if (body.status!=='recieved'&&body.status!=='ok') {
                                console.log('ERROR: DONE for '+appendedResults.gameId+' didnt stick in evaluator');
                            }
                            
                        }
                    }
                );
            } else {
                console.log('ERROR: failed to retrieve gdl and hlgdl: '+err);
            }
        });
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
              } else if (error){
                console.log('get ERROR: '+error);
              } else {
                console.log('get resp code:'+response.statusCode);
              }
              callback();
            });
        } else {
            callback();
        }
    };

    self.savePlayerTypeSkills = function(skillMap) {
        self.database.savePlayerTypeSkills(skillMap,function(err) {if (err) console.log('ERROR saving skills: '+err);});
    }

    self.getPlayerTypeSkill = function(type,callback) {
        self.database.getPlayerTypeSkill(type,callback);
    }
};   /*  END Controller Application.  */



/**
 *  main():  Main code.
 */
var zapp = new ControllerApp(process.argv[2],+process.argv[3]);
zapp.initialize();
zapp.start();


module.exports =  function() {
    
    //https://jsfiddle.net/GRIFFnDOOR/r7tvg/
    /////////////////////////////////////////
    function Node (data, priority) {
        this.data = data;
        this.priority = priority;
    }
    Node.prototype.toString = function(){return this.priority}

    // takes an array of objects with {data, priority}
    function PriorityQueue (arr) {
        this.heap = [null];
        if (arr) for (i=0; i< arr.length; i++)
            this.push(arr[i].data, arr[i].priority);
    }

    PriorityQueue.prototype = {
        push: function(data, priority) {
            var node = new Node(data, priority);
            this.bubble(this.heap.push(node) -1);      
        },
        
        // removes and returns the data of highest priority
        pop: function() {
            if (this.heap.length-1>0) {
                var topVal = this.heap[1].data;
                this.heap[1] = this.heap.pop();
                this.sink(1); return topVal;
            }  else
                return null;
        },
        
        top: function() {
            return this.heap[1].data;
        },
        
        
        // bubbles node i up the binary tree based on
        // priority until heap conditions are restored
        bubble: function(i) {
            while (i > 1) { 
                var parentIndex = i >> 1; // <=> floor(i/2)
                
                // if equal, no bubble (maintains insertion order)
                if (!this.isHigherPriority(i, parentIndex)) break;
                
                this.swap(i, parentIndex);
                i = parentIndex;
        }   },
            
        // does the opposite of the bubble() function
        sink: function(i) {
            while (i*2 < this.heap.length) {
                // if equal, left bubbles (maintains insertion order)
                var leftHigher = !this.isHigherPriority(i*2 +1, i*2);
                var childIndex = leftHigher? i*2 : i*2 +1;
                
                // if equal, sink happens (maintains insertion order)
                if (this.isHigherPriority(i,childIndex)) break;
                
                this.swap(i, childIndex);
                i = childIndex;
        }   },
            
        // swaps the addresses of 2 nodes
        swap: function(i,j) {
            var temp = this.heap[i];
            this.heap[i] = this.heap[j];
            this.heap[j] = temp;
        },
        
        length: function() {
            return this.heap.length-1;
        },
            
        // returns true if node i is higher priority than j
        // I'm assuming higher score = higher priority
        isHigherPriority: function(i,j) {
            return this.heap[i].priority > this.heap[j].priority;
        }
    }
    /////////////////////////////////////////
       
    function GameCord(owner,database) {
        //////////////////
        this.version=0.0;
        //////////////////
        
        this.players=[{id:0, type:'random', busy:false, name:'built-in random', gdlVersion:2, skill:0, host:'none', port:-1}];
        this.allPlayerTypes=['random'];
        this.matchesBeingPlayed=[];
        this.queue=new PriorityQueue();
        this.curGame=null;
        this.playerIdCounter=1;
        this.exec = require('child_process').exec;
        this.owner=owner;
        this.database=database;
        var self = this;
        this.nextMatchId = null;
        this.database.getMaxMatchId(function(matchId){self.nextMatchId=matchId;});
        this.numMatchesBeingPlayed=0;
    }
    
    GameCord.prototype.addPlayer = function (method,host,port,name,skillLevel,gdlVersion,readjust)  {//level 0 is random
        if (method===undefined){
            console.log('ERROR: method must be defined for player.');
            return 'ERROR: method must be defined for player.';
        }
        method=method.toLowerCase();
        if (host===undefined){
            console.log('ERROR: host must be defined for player.');
            return 'ERROR: host must be defined for player.';
        }
        if (port===undefined){
            console.log('ERROR: port must be defined for player.');
            return 'ERROR: port must be defined for player.';
        }
        if (readjust===undefined)
            readjust=true;
        if (name===undefined)
            name='unnamed';
        if (skillLevel===undefined)
            skillLevel=1;
        if (gdlVersion===undefined)
            gdlVersion=1;
        this.players.push({
                            id:this.playerIdCounter++,
                            type:method,
                            host:host, 
                            port:port, 
                            gdlVersion:gdlVersion, 
                            name:name, 
                            skill:skillLevel, 
                            busy:false
                          });
        if (this.allPlayerTypes.indexOf(method)==-1) {
            this.allPlayerTypes.push(method);
        }
        if (readjust) {
            this.createPlaySchedule();
        }
        console.log('Added player '+name+' ('+host+':'+port+') of type '+method);
        return 'ok';
    }
    
    GameCord.prototype.createPlaySchedule = function () {
        
        
        var self = this;
        function allCombinationsOfPlayerTypes(owner,count,startclock,playclock,maxSteps,gameId,gdlVersion,ret,depth,allPlayerTypes,soFar) {
            if (soFar === undefined)
                soFar=[];
            for (var pn=0; pn<allPlayerTypes.length; pn++) {
                var pType=allPlayerTypes[pn];
                //if (soFar.indexOf(pid)==-1) {
                    if (depth>1) {
                        allCombinationsOfPlayerTypes(owner,count,startclock,playclock,maxSteps,gameId,gdlVersion,ret,depth-1,allPlayerTypes.slice(pn+1),soFar.concat([pType]));
                    } else {
                        ret.push({
                                    id:(self.nextMatchId++), 
                                    numToPlay:count, 
                                    playerTypes:soFar.concat([pType]),
                                    startclock:startclock,
                                    playclock:playclock,
                                    maxSteps:maxSteps,
                                    gameId:gameId,
                                    gdlVersion:gdlVersion,
                                    beingPlayed:false,
                                    finished:false,
                                    owner:owner
                                });
                    }
                //}
            }
        }
        
        self.Schedule = function(gameMeta) {
            this.matches=[];
            if (gameMeta.numPlayers > self.allPlayerTypes.length){
                console.log('ERROR: game requires more player types ('+gameMeta.numPlayers+') than are connected ('+self.allPlayerTypes.length+')');
            } else {
                var numOfRuns;//how many repitions of each scenario
                var startclock;//how long to the players have to analyze the game before playing 
                var playclock;//how long players have to make their move
                var maxSteps;//maximum number of turns
                var valid=true;
                if ( gameMeta.testLength==='short' || 
                     gameMeta.testLength==='s' ||
                     gameMeta.testLength==='quick') {
                    numOfRuns=1;
                    startclock=60;
                    playclock=15;
                    maxSteps=120;
                } else if (gameMeta.testLength==='med' || 
                           gameMeta.testLength==='medium' || 
                           gameMeta.testLength==='m') {
                    numOfRuns=5;
                    startclock=75;
                    playclock=25;
                    maxSteps=160;
                } else if (gameMeta.testLength==='long' || 
                           gameMeta.testLength==='l' || 
                           gameMeta.testLength==='full') {
                    numOfRuns=10;
                    startclock=100;
                    playclock=30;
                    maxSteps=200;
                } else {
                    console.log('ERROR: game '+gameMeta.id+' '+gameMeta.name+' has an invalid testLength: '+gameMeta.testLength);
                    valid = false;
                }
                if (valid)
                    allCombinationsOfPlayerTypes(this,numOfRuns,startclock,playclock,maxSteps,gameMeta.id,gameMeta.gdlVersion,this.matches,gameMeta.numPlayers,self.allPlayerTypes);///match has a boolean played and a list playerTypes
                //console.log('matches');
                //console.log(this.matches);
            }
            this.matchesByPlayerTypes = {};
        };
        this.Schedule.prototype.byPlayerTypes = function(playerTypes) {
            var ret = [];
            for (playerType of playerTypes) {
                if (this.matchesByPlayerTypes[playerType] === undefined) {
                    this.matchesByPlayerTypes[playerType]=[];
                    for (var m of this.matches) {
                        if (m.playerTypes.indexOf(playerType)!=-1)
                            this.matchesByPlayerTypes[playerType].push(m);
                    }
                }
                ret = ret.concat(this.matchesByPlayerTypes[playerType]);
            }
            return ret;
        };
        
        this.Schedule.prototype.allDone = function() {
            for (var m of this.matches) {
                if (m.finished==false)
                    return false;
            }
            return true;
        };
    }
    
    GameCord.prototype.enqueue = function(meta) {
        var err='ok';
        if (meta.testLength===undefined)
            meta.testLength='med';
        else
            meta.testLength=meta.testLength.toLowerCase();
        if (meta.numPlayers===undefined) {
            err = 'ERROR: numPlayers not included in game '+meta.id+' '+meta.name;
            console.log(err);
            return err;
        }
        if (meta.id===undefined) {
            err = 'ERROR: id not included in game '+meta.name;
            console.log(err);
            return err;
        }
        if (meta.intrinsicScore===undefined) {
            err = 'ERROR: intrinsicScore not included in game '+meta.id+' '+meta.name;
            console.log(err);
            return err;
        }
        if (meta.numPlayers > this.allPlayerTypes.length) {
            err = 'ERROR: game requires more player types ('+meta.numPlayers+') than are connected ('+this.allPlayerTypes.length+')';
            console.log(err);
            return err;
        }
        if ( meta.testLength!=='short' && 
             meta.testLength!=='s' &&
             meta.testLength!=='quick' &&
             meta.testLength!=='med' &&
             meta.testLength!=='medium' && 
             meta.testLength!=='m' && 
             meta.testLength!=='long' && 
             meta.testLength!=='l' && 
             meta.testLength!=='full') {
            err = 'ERROR: game '+meta.id+' '+meta.name+' has an invalid testLength: '+meta.testLength;
            console.log(err);
            return err;
        }
        
        if (meta.gdlVersion===undefined) {
            console.log('WARNING: gdlVersion not included in game '+meta.id+' '+meta.name+', assuming 1');
            meta.gdlVersion=1;
        }
        
        
        if (this.curGame === null) {
            console.log('Set current game '+meta.id+': '+meta.name);
            this.curGame = meta;
        } else {
            console.log('Enqueued game '+meta.id+': '+meta.name);
            this.queue.push(meta, meta.intrinsicScore);
        }
        
        if (this.queue.length() < 2) {
            this.kickMatches();
        }
        return err;
        
    };
    
    GameCord.prototype.beginMatch = function (m,gameMeta) {
        if (m.beingPlayed==false && m.numToPlay>0) {
            console.log('Beginning match '+m.id); 
            this.numMatchesBeingPlayed+=1;
            var self=this;
            m.beingPlayed=true;
            for (p of this.players) {
                if (p.id!==0 && m.playerIds.indexOf(p.id)!=-1) {
                    p.busy=true;
                }
            }
            //run iterations on all combinations of player positions
            //I've lumped them like this for both efficency and bookkeeping reasons
            
            //from http://stackoverflow.com/questions/9960908/permutations-in-javascript
            var permArr = [],
              usedChars = [];

            function permute(input) {
              var i, ch;
              for (i = 0; i < input.length; i++) {
                ch = input.splice(i, 1)[0];
                usedChars.push(ch);
                if (input.length == 0) {
                  permArr.push(usedChars.slice());
                }
                permute(input);
                input.splice(i, 0, ch);
                usedChars.pop();
              }
              return permArr
            };
            ///////////////////////////
            var allOrders=permute(m.playerIds);
            
            self.database.gdlFileLocation(gameMeta.id, function(gameFileLocation) {
                self.playMatch(m,allOrders,gameFileLocation);
            });
            
            return true;
        }
        else {
            console.log("ERROR? trying to beginMatch() for match ("+m.id+") being played?");
            return false;
        }
    }
    
    GameCord.prototype.playMatch = function (m,allOrders,gameLoc,leftToPlayWith,thesePlayers) {
        var self=this;
        var thisGamePlayers;
        if (leftToPlayWith!==undefined && leftToPlayWith>0) {
            thisGamePlayers = thesePlayers;
        } else {
            thisGamePlayers = allOrders.pop();
            leftToPlayWith = m.numToPlay;
        }
        var rep=(m.numToPlay-leftToPlayWith);
        var matchId = (m.id)+'_'+allOrders.length+'_'+rep;
        var startGameControllerCommand = 'java -jar ./gamecontroller/gamecontroller-cli.jar '+matchId+' '+gameLoc+' '+m.startclock+' '+m.playclock+' '+m.maxSteps+' '+m.gdlVersion;
        //startGameControllerCommand += ' -printxml '+OUTPUTDIR+' '+XSLT;//TODO possibly
        for (var roleIndex=1; roleIndex<=thisGamePlayers.length; roleIndex++) {
            if (self.players[thisGamePlayers[roleIndex-1]].type==='random')
                startGameControllerCommand += ' -random '+roleIndex;
            else {
                var name = self.players[thisGamePlayers[roleIndex-1]].name;
                var host = self.players[thisGamePlayers[roleIndex-1]].host;
                var port = self.players[thisGamePlayers[roleIndex-1]].port;
                var gdlVersion = self.players[thisGamePlayers[roleIndex-1]].gdlVersion;
                startGameControllerCommand += ' -remote '+roleIndex+' '+name+' '+host+' '+port+' '+gdlVersion;
            }
        }
        console.log('Starting gameSim:\n  '+startGameControllerCommand);
        self.exec(startGameControllerCommand, function (error, stdout, stderr) {
            if (error !== null) {
                console.log('exec ERROR: ' + error);
            } else {
                console.log('Ending gameSim: '+ matchId);
                //record from stdout
                var thisGamePlayerInfo=[];
                for (var playerId of thisGamePlayers) {
                    thisGamePlayerInfo.push({
                                            id:self.players[playerId].id,
                                            name:self.players[playerId].name,
                                            skill:self.players[playerId].skill,
                                            type:self.players[playerId].type
                                         });
                }
                self.owner.sendGameResults({matchId:m.id, playerOrder:allOrders.length, rep:rep, players:thisGamePlayerInfo, gameId:m.gameId,printout:stdout});
            }
            if (allOrders.length>0 || leftToPlayWith>1)
                self.playMatch(m,allOrders,gameLoc,leftToPlayWith-1,thisGamePlayers);
            else {//release the players
                self.wrapUpMatch(m);
            }
        });
    }
    
    GameCord.prototype.wrapUpMatch = function (m) {
        m.finished=true;
        this.numMatchesBeingPlayed-=1;
        for (p of this.players) {
            if (p.id!==0 && m.playerIds.indexOf(p.id)!=-1) {
                p.busy=false;
            }
        }
        
        //inform evaluator
        if (m.owner.allDone())
            this.owner.sendGameDone({gameId:m.gameId});
        
        //The second term is just a catch-all in case we hit a funny case
        if (this.findNextMatch(m.playerTypes) || this.numMatchesBeingPlayed==0)
            this.kickMatches();
        
        
    }
    
    GameCord.prototype.kickMatches = function () {
        //console.log('Kicking matches');
        while (this.findNextMatch()) {}
    }
    
    GameCord.prototype.findNextMatch = function (playerTypes) {
        if (this.curGame===null) {
            //console.log('no game to play');
            return false;
        }
            
        if (this.curGame.schedule == undefined) {
            if (this.curGame.numPlayers == undefined) {
                console.log('ERROR: numPlayers is undefined');
                return null;
                //this.curGame.numPlayers = this.curGame.numPlayers;//GDLReader.findNumPlayers(database.gdl(this.curGame.id));
            }
            this.curGame.schedule = new this.Schedule(this.curGame);
        }
        var m = this.getNextMatch(this.curGame.schedule,playerTypes);
        if (m==='done') {
            m=null;
            console.log('Game '+this.curGame.id+' '+this.curGame.name+' is all running/done');
            this.curGame = this.queue.pop();
            return this.findNextMatch(playerTypes);
        }
        if (m == null) {
            if (this.queue.length>0) {
                var nextGame = this.queue.top();
                if (nextGame.schedule == undefined) {
                    if (nextGame.numPlayers == undefined) {
                        console.log('ERROR: numPlayers is undefined');
                        return null;
                        //this.queue.top.numPlayers = this.curGame.numPlayers;//GDLReader.findNumPlayers(database.gdl(this.queue.top.id));
                    }
                    nextGame.schedule = new this.Schedule(nextGame);
                }
                m = this.getNextMatch(nextGame.schedule,playerTypes);
                if (m != null) {
                    return this.beginMatch(m,nextGame);;
                }
            } else {
                console.log('no match found for game '+this.curGame.id);
                return false;
            }
        }
        else {
            return this.beginMatch(m,this.curGame);;
        }
        
        
    };
    
    GameCord.prototype.getNextMatch = function (schedule,playerTypes) {
        var searchSpace;
        var noneNeedingPlayed=true;
        if (playerTypes != undefined) {
            searchSpace = schedule.byPlayerTypes(playerTypes);
            noneNeedingPlayed=false;
        }
        else {
            searchSpace = schedule.matches;
        }
        
        console.log('getNextMatch searchSpace.length='+searchSpace.length);
        
        for (var m of searchSpace) {
            if (m.beingPlayed==false) {
                noneNeedingPlayed=false;
                var allFree=true;
                var freePlayerIds=[];
                for (var playerType of m.playerTypes) {
                    var aFree=null;
                    if (playerType==='random') {
                        aFree=0;
                    }
                    else {
                        for (var player of this.players) {
                            if (!player.busy && player.type===playerType) {
                                aFree=player.id;
                                break;
                            }
                        }
                    }
                    if (aFree===null) {
                        allFree=false;
                        break;
                    } else {
                        freePlayerIds.push(aFree);
                    }
                }
                if (allFree) {
                    m.playerIds=freePlayerIds;
                    return m;
                }
            }
        }
        if (noneNeedingPlayed)
            return 'done';
        else
            return null;
    };
    
    return GameCord;
};

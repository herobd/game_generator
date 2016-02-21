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
            var topVal = this.heap[1].data;
            this.heap[1] = this.heap.pop();
            this.sink(1); return topVal;
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
            return this.heap.length;
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
        function allCombinationsOfPlayerTypes(count,startclock,playclock,gameId,ret,depth,allPlayerTypes,soFar) {
            if (soFar === undefined)
                soFar=[];
            for (var pn=0; pn<allPlayerTypes.length; pn++) {
                var pType=allPlayerTypes[pn];
                //if (soFar.indexOf(pid)==-1) {
                    if (depth>1) {
                        allCombinationsOfPlayerTypes(count,startclock,playclock,gameId,ret,depth-1,allPlayerTypes.splice(pn+1),soFar.concat([pType]));
                    } else {
                        ret.push({
                                    id:(self.nextMatchId++), 
                                    numToPlay:count, 
                                    playerTypes:soFar.concat([pType]),
                                    startclock:startclock,
                                    playclock:playclock,
                                    gameId:gameId,
                                    beingPlayed:false
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
                if ( gameMeta.testLength==='short' || 
                     gameMeta.testLength==='s' ||
                     gameMeta.testLength==='quick') {
                    numOfRuns=1;
                    startclock=60;
                    playclock=20;
                } else if (gameMeta.testLength==='med' || 
                           gameMeta.testLength==='medium' || 
                           gameMeta.testLength==='m') {
                    numOfRuns=5;
                    startclock=75;
                    playclock=25;
                } else if (gameMeta.testLength==='long' || 
                           gameMeta.testLength==='l' || 
                           gameMeta.testLength==='full') {
                    numOfRuns=10;
                    startclock=100;
                    playclock=30;
                } else {
                    console.log('ERROR: game '+gameMeta.id+' '+gameMeta.name+' has an invalid testLength: '+gameMeta.testLength);
                }
                allCombinationsOfPlayerTypes(numOfRuns,startclock,playclock,gameMeta.id,this.matches,gameMeta.numPlayers,self.allPlayerTypes);///match has a boolean played and a list playerTypes
                
            }
            this.matchesByPlayerTypes = {};
        }
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
        }
        
    }
    
    GameCord.prototype.enqueue = function(meta) {
        var err='ok';
        if (meta.testLength===undefined)
            meta.testLength='long';
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
        if (meta.score===undefined) {
            err = 'ERROR: score not included in game '+meta.id+' '+meta.name;
            console.log(err);
            return err;
        }
        if (meta.gdlVersion===undefined) {
            console.log('WARNING: gdlVersion not included in game '+meta.id+' '+meta.name+', assuming 1');
            meta.gdlVersion=1;
        }
        
        if (this.curGame == null)
            this.curGame = meta;
        else
            this.queue.push(meta, meta.score);
        console.log('Enqueued game '+meta.id+': '+meta.name);
        if (this.queue.length() < 2) {
            this.kickMatches();
        }
        return err;
        
    };
    
    GameCord.prototype.beginMatch = function (m,gameMeta) {
        if (m.beingPlayed==false && m.numToPlay>0) {
            var self=this;
            m.played=true;
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
            
            
        }
        else
            console.log("ERROR? trying to beginMatch() for match being played?");
    }
    
    GameCord.prototype.playMatch = function (m,allOrders,gameLoc,leftToPlayWith,thesePlayers) {
        var self=this;
        var thisGamePlayers;
        if (leftToPlayWith!==undefined && leftToPlayWith>0) {
            thisGamePlayers = thesePlayers;
            leftToPlayWith-=1;
        } else {
            thisGamePlayers = allOrders.pop();
            leftToPlayWith = m.numToPlay;
        }
        var rep=(m.numToPlay-leftToPlayWith);
        var matchId = (m.id)+'_'+allOrders.length+'_'+rep;
        var startGameControllerCommand = 'java -jar ./gamecontroller/gamecontroller-cli.jar '+matchId+' '+gameLoc+' '+m.startclock+' '+m.playclock;
        //startGameControllerCommand += ' -printxml '+OUTPUTDIR+' '+XSLT;//TODO possibly
        for (var roleIndex=1; roleIndex<=thisGamePlayers.length; roleIndex++) {
            if (self.players[thisGamePlayers[roleIndex-1]].method='random')
                startGameControllerCommand += ' -random '+roleIndex;
            else {
                var name = self.players[thisGamePlayers[roleIndex-1]].name;
                var host = self.players[thisGamePlayers[roleIndex-1]].host;
                var port = self.players[thisGamePlayers[roleIndex-1]].port;
                var gdlVersion = self.players[thisGamePlayers[roleIndex-1]].gdlVersion;
                startGameControllerCommand += ' -remote '+roleIndex+' '+name+' '+host+' '+port+' '+gdlVersion;
            }
        }
        console.log('Starting game:\n  '+startGameControllerCommand);
        self.exec(startGameControllerCommand, function (error, stdout, stderr) {
            if (error !== null) {
                console.log('exec ERROR: ' + error);
            } else {
                //record from stdout
                var thisGamePlayersSkill=[]
                for (playerId of thisGamePlayers) {
                    thisGamePlayersSkill.push(self.players[playerId].skill);
                }
                owner.sendGameResults({matchId:m.id, playerSkills:thisGamePlayersSkill, gameId:m.gameId,printout:stdout});
            }
            if (allOrders.length>0 || leftToPlayWith>0)
                self.playMatch(m,allOrders,gameLoc,leftToPlayWith,thisGamePlayers);
            else {//release the players
                self.wrapUpMatch(m);
            }
        });
    }
    
    GameCord.prototype.wrapUpMatch = function (m) {
        for (p of this.players) {
            if (p.id!==0 && m.playerIds.indexOf(p.id)!=-1) {
                p.busy=false;
            }
        }
        if (this.findNextMatch(m.playerTypes))
            this.kickMatches();
        
        //TODO inform evaluator
    }
    
    GameCord.prototype.kickMatches = function () {
        while (this.findNextMatch()) {}
    }
    
    GameCord.prototype.findNextMatch = function (playerTypes) {
        if (this.curGame.schedule == undefined) {
            if (this.curGame.numPlayers == undefined) {
                console.log('ERROR: numPlayers is undefined');
                return null;
                //this.curGame.numPlayers = this.curGame.numPlayers;//GDLReader.findNumPlayers(database.gdl(this.curGame.id));
            }
            this.curGame.schedule = new this.Schedule(this.curGame);
        }
        var m = this.getNextMatch(this.curGame.schedule,playerTypes);
        if (m == null) {
            if (this.queue.length>0) {
                if (this.queue.top.schedule == undefined) {
                    if (this.queue.top.numPlayers == undefined) {
                        console.log('ERROR: numPlayers is undefined');
                        return null;
                        //this.queue.top.numPlayers = this.curGame.numPlayers;//GDLReader.findNumPlayers(database.gdl(this.queue.top.id));
                    }
                    this.queue.top.schedule = new this.Schedule(this.queue.top);
                }
                m = this.getNextMatch(this.queue.top.schedule,playerTypes);
                if (m != null) {
                    this.beginMatch(m,this.queue.top);
                    return true;
                }
            } else {
                console.log('no match found for game '+this.curGame.id);
                return false;
            }
        }
        else {
            this.beginMatch(m,this.curGame);
            return true;
        }
        
        
    };
    
    GameCord.prototype.getNextMatch = function (schedule,playerTypes) {
        var searchSpace;
        if (playerTypes != undefined) {
            searchSpace = schedule.byPlayerTypes(playerTypes);
        }
        else {
            searchSpace = schedule.matches;
        }
        for (var m of searchSpace) {
            if (m.beingPlayed==false) {
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
        //else
        return null;
    };
    
    return GameCord;
};

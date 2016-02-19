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
            return this.heap[i].length;
        },
            
        // returns true if node i is higher priority than j
        // I'm assuming higher score = higher priority
        isHigherPriority: function(i,j) {
            return this.heap[i].priority > this.heap[j].priority;
        }
    }
    /////////////////////////////////////////
       
    function GameCord(database) {
        this.players=[{id:0, type:'random', busy:false, name:'built-in random', gdlVersion:2, skill:0, host:'none', port:-1}];
        this.matchesBeingPlayed=[];
        this.queue=new PriorityQueue();
        this.curGame=null;
        this.playerIdCounter=1;
        this.exec = require('child_process').exec;
        this.database=database;
        this.nextMatchId = this.database.getMaxMatchId();
    }
    
    GameCord.prototype.addPlayer = function (method,host,port,gdlVersion,name,skillLevel,readjust)  {//level 0 is random
        if (method===undefined){
            console.log('ERROR: method must be defined for player.');
            return;
        }
        if (readjust===undefined)
            readjust=true;
        if (name===undefined)
            name='unnamed';
        if (skillLevel===undefined)
            skillLevel=1;
        players.push({id:this.playerIdCounter++,type:method,host:host, port:port, gdlVerison:gdlVersion, name:name, skill:skillLevel, busy:false});
        if (readjust) {
            this.createPlaySchedule();
        }
    }
    
    GameCord.prototype.createPlaySchedule = function () {
        
        
        var self = this;
        function allCombinationsOfPlayerTypes(count,startclock,playclock,ret,depth,allPlayerTypes,soFar) {
            if (soFar === undefined)
                soFar=[];
            for (var pn=0; pn<allPlayerTypes.length; pn++) {
                var pType=allPlayerTypes[pn];
                //if (soFar.indexOf(pid)==-1) {
                    if (depth>1) {
                        allCombinationsOfPlayerTypes(ret,depth-1,allPlayerTypes.splice(pn+1),soFar.concat([pType]));
                    } else {
                        ret.push({
                                    id:(self.nextMatchId++), 
                                    numToPlay:count, 
                                    playerTypes:soFar.concat([pType])
                                    startclock:startclock,
                                    playclock:playclock
                                });
                    }
                //}
            }
        }
        
        this.Schedule = function(numOfRuns,startclock,playclock,numPlayers) {
            this.matches=[];
            if (numPlayers > self.players){
                console.log('ERROR: game requires more players than are connected');
            } else {
                allCombinationsOfPlayerTypes(numOfRuns,startclock,playclock,this.matches,numPlayers,self.allPlayerTypes);///match has a boolean played and a list playerTypes
                
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
        if (meta.id && meta.score) {
            //meta.schedule = new this.Schedule();
            if (this.curGame == null)
                this.curGame = meta;
            else
                this.queue.push(meta, meta.score);
            console.log('Enqueued game '+meta.id+': '+meta.name);
            if (this.queue.length() < 2) {
                this.findNextMatch();
            }
        }
        
    };
    
    GameCord.prototype.beginMatch = function (m,gameMeta) {
        if (m.toPlay>0) {
        
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
            var allOrders=permute(m.playerIds);
            
            this.playMatch(m,allOrders,database.gdlFileLocation(gameMeta.id));
            
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
        var matchId = (m.id)+'_'+(m.numToPlay-leftToPlayWith);
        var startGameControllerCommand = 'java -jar ./gamecontroller.jar '+matchId+' '+gameLoc+' '+m.startclock+' '+m.playclock;
        //startGameControllerCommand += ' -printxml '+OUTPUTDIR+' '+XSLT;//TODO possibly
        for (var roleIndex=1; roleIndex<=thisGamePlayers.length; roleIndex++) {
            if (self.players[thisGamePlayers[roleIndex-1]].method='random')
                startGameControllerCommand += ' -random';
            else {
                var name = self.players[thisGamePlayers[roleIndex-1]].name;
                var host = self.players[thisGamePlayers[roleIndex-1]].host;
                var port = self.players[thisGamePlayers[roleIndex-1]].port;
                var gdlVersion = self.players[thisGamePlayers[roleIndex-1]].gdlVersion;
                startGameControllerCommand += ' -remote '+roleIndex+' '+name+' '+host+' '+port+' '+gdlVersion;
            }
        }
        
        self.exec(startGameControllerCommand, function (error, stdout, stderr) {
            if (error !== null) {
                console.log('exec error: ' + error);
            }
            //TODO record from stdout
            
            if (allOrders.length>0 || toPlayWith>0)
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
        this.findNextMatch(m.playerTypes);
        
        //TODO inform evaluator
    }
    
    GameCord.prototype.findNextMatch = function (playerTypes) {
        if (this.curGame.schedule == undefined) {
            if (this.curGame.numPlayers == undefined) {
                this.curGame.numPlayers = GDLReader.findNumPlayers(database.gdl(this.curGame.id));
            }
            this.curGame.schedule = new this.Schedule(this.curGame.numPlayers);
        }
        var m = this.getNextMatch(this.curGame.schedule,playerTypes);
        if (m == null) {
            if (this.queue.top.schedule == undefined) {
                if (this.queue.top.numPlayers == undefined) {
                    this.queue.top.numPlayers = GDLReader.findNumPlayers(database.gdl(this.queue.top.id));
                }
                this.queue.top.schedule = new this.Schedule(this.queue.top.numPlayers);
            }
            m = this.getNextMatch(this.queue.top.schedule,playerTypes);
            if (m != null) {
                this.beginMatch(m,this.queue.top);
            }
        }
        else {
            this.beginMatch(m,this.curGame);
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
            if (m.played=false) {
                var allFree=true;
                var freePlayerIds;
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
    
    return GameCord
};

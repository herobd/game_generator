define( function() {
    
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
       
    function GameCord() {
        this.players=[];
        this.matchesBeingPlayed=[];
        this.queue=new PriorityQueue();
        this.curGame=null;
    }
    
    GameCord.prototype.addPlayer = function (address,name,skill_level,method,readjust)  {//level 0 is random
        if (method===undefined)
            method='unknown';
        if (readjust===undefined)
            readjust=true;
        players.push({address:address, name:name, skill:skill_level, busy:false});
        if (readjust) {
            this.createPlaySchedule();
        }
    }
    
    GameCord.prototype.createPlaySchedule = function () {
        //TODO
        
        this.Schedule = function(numPlayers) {
            this.matches=[];///
        }
    }
    
    GameCord.prototype.enqueue = function(meta) {
        if (meta.id && meta.score) {
            //meta.schedule = new this.Schedule();
            if (this.curGame == null) {
                this.curGame = meta;
            }
            else
                this.queue.push(meta, meta.score);
            if (this.queue.length() < 2) {
                this.findNextMatch();
            }
        }
        
    };
    
    GameCord.prototype.beginMatch = function (m,gameMeta) {
        if (m.played.false) {
        
            m.played=true;
            //...run iterations on all combinations of player positions
        }
        else
            console.log("ERROR? trying to beginMatch() for match being played?");
    }
    
    GameCord.prototype.findNextMatch = function (playerId) {
        if (this.curGame.schedule == undefined) {
            if (this.curGame.numPlayers == undefined) {
                this.curGame.numPlayers = GDLReader.findNumPlayers(database.gdl(this.curGame.id));
            }
            this.curGame.schedule = new this.Schedule(this.curGame.numPlayers);
        }
        var m = this.getNextMatch(this.curGame.schedule,playerId);
        if (m == null) {
            if (this.queue.top.schedule == undefined) {
                if (this.queue.top.numPlayers == undefined) {
                    this.queue.top.numPlayers = GDLReader.findNumPlayers(database.gdl(this.queue.top.id));
                }
                this.queue.top.schedule = new this.Schedule(this.queue.top.numPlayers);
            }
            m = this.getNextMatch(this.queue.top.schedule,playerId);
            if (m != null) {
                this.beginMatch(m,this.queue.top);
            }
        }
        else {
            this.beginMatch(m,this.curGame);
        }
        
        
    };
    
    GameCord.prototype.getNextMatch = function (schedule,playerId) {
        var searchSpace;
        if (playerId != undefined) {
            searchSpace = schedule.byPlayer[playerId];
        }
        else {
            searchSpace = schedule.matches;
        }
        for (var m of searchSpace) {
            if (m.played=false) {
                var allFree=true;
                for (var playerId of m.players) {
                    if (this.players[playerId].busy) {
                        allFree=false;
                        break;
                    }
                }
                if (allFree) {
                    return m;
                }
            }
        }
        //else
        return null;
    };
    
    return GameCord
});

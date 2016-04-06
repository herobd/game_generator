module.exports = function() {

    var cluster = require('hierarchical-clustering');
    
    function pieceCounter(i0,i1,board,players) {
        this.RE_piece = /(\w+)_(\w+)/;
        //this.player = p.match(RE_piece)[1];
        //this.piece = p.match(RE_piece)[2];
        //this.allies={total:0};
        //this.enemies={total:0};
        this.players={};
        //console.log(players);
        for (var p of players) {
            this.players[p.toLowerCase()]={total:0};
        }
        this.board=board;
        this.i0=+i0;
        this.i1=+i1;
        
    }
    pieceCounter.prototype.add = function(pos) {
        var toAdd;
        var pp=this.getPlayer(pos);
        if (pp!=null){
            
            toAdd=this.players[pp];
            var pieceType = this.getPiece(pos);
            if (toAdd.hasOwnProperty(pieceType))
                toAdd[pieceType]+=1;
            else
                toAdd[pieceType]=1;
            toAdd.total+=1;
        }
    }
    pieceCounter.prototype.getPiece = function(dir) {
        if (this.board.length>this.i0+dir[0] && this.i0+dir[0]>=0 &&
            this.board[this.i0+dir[0]].length>this.i1+dir[1] && this.i1+dir[1]>=0) {
            var p=this.board[this.i0+dir[0]][this.i1+dir[1]];
            if (p===undefined)
                console.log('ERROR p==undefined, '+this.i0+dir[0]+' '+this.i1+dir[1]);
            var m= p.match(this.RE_piece);
            if (m!=null)
                return m[2];//.toLowerCase();
        }
        return null;
    }
    pieceCounter.prototype.getPlayer = function(dir) {
        if (this.board.length>this.i0+dir[0] && this.i0+dir[0]>=0 &&
            this.board[this.i0+dir[0]].length>this.i1+dir[1] && this.i1+dir[1]>=0) {
            var p=this.board[this.i0+dir[0]][this.i1+dir[1]];
            if (p===undefined)
                console.log('ERROR p==undefined, '+this.i0+dir[0]+' '+this.i1+dir[1]);
            var m= p.match(this.RE_piece);
            if (m!=null)
                return m[1];//.toLowerCase();
        }
        return null;
    }
    pieceCounter.prototype.sqDistancePart = function(countedA,countedB) {
        var ret=0;
        for (var prop in countedA) {
            if (countedA.hasOwnProperty(prop)) {
                if (countedB.hasOwnProperty(prop))
                    ret += Math.pow(countedA[prop]-countedB[prop],2);
                else
                    ret += Math.pow(countedA[prop],2);
            }
        }
        for (var prop in countedB) {
            if (countedB.hasOwnProperty(prop)) {
                if (!countedA.hasOwnProperty(prop))
                    ret += Math.pow(countedB[prop],2);
            }
        }
        return ret;
    }
    pieceCounter.prototype.sqDistance = function(other) {
        if (this.player!==other.player) {
            return 0;
        }
        
        var ret=0;
        //ret+=this.sqDistancePart(this.allies,other.allies);
        //ret+=this.sqDistancePart(this.enemies,other.enemies);
        for (var i=0; i<this.players.length; i++) {
            ret+=this.sqDistancePart(this.players[i],other.players[i]);
        }
        
        return ret;
    }
    
    function Description(i0,i1,p,board,hlgdl) {
        
        this.hlgdl=hlgdl;
        this.i0=+i0;
        this.i1=+i1;
        var RE_piece = /(\w+)_(\w+)/;
        //console.log(p);
        this.player = p.match(RE_piece)[1];
        this.piece = p.match(RE_piece)[2];
        function makePieceCounter() {
            return new pieceCounter(this.i0,this.i1,board,hlgdl.players);
        }
        
        
        //var thisPiece=makePieceCounter();
        //thisPiece.add([0,0]);
        this.descAllOr=[];//A graph will only have one orientation
        if (hlgdl.board.macroType=='Grid') {
            var orientations;
            if (hlgdl.board.tileType=='Square') {
                orientations = [[[0,-1], [0, 1], [-1,0], [ 1,0],[-1,-1], [-1, 1], [ 1,-1], [ 1, 1]],
                                [[0, 1], [0,-1], [-1,0], [ 1,0],[-1, 1], [-1,-1], [ 1, 1], [ 1,-1]],
                                [[0,-1], [0, 1], [ 1,0], [-1,0],[ 1,-1], [ 1, 1], [-1,-1], [-1, 1]],
                                [[0, 1], [0,-1], [ 1,0], [-1,0],[ 1, 1], [ 1,-1], [-1, 1], [-1,-1]]];
                var revOrientations=[]
                for (var directions of orientations) {
                    var revDir=[]
                    for (dir of directions) {
                        revDir.push([dir[1],dir[0]]);
                    }
                    revOrientations.push(revDir);
                }
                orientations=orientations.concat(revOrientations);
                //desc:n,s,w,e,nw,ne,sw,se,count_p1,count_p2,count_p3,count_n,count_s,count_w,count_e,count_nw,count_ne,count_sw,count_se,count_knight
                
            } else {
                console.log('ERROR, tile type '+hlgdl.board.tileType+' not implemented for Description.');
                this.descAllOr=null;
                return;
            }
            
            for (var directions of orientations) {
                
            
                var desc=[];
                for (dir of directions) {
                    var count=makePieceCounter();
                    count.add(dir);
                    desc.push(count);
                }
                
                //var hoods=[];
                
                
                for (var hoodSize=1; hoodSize<=3; hoodSize++) {
                    var count=makePieceCounter();
                    for (var h0=-1*hoodSize; h0<=hoodSize; h0++) {
                        
                        count.add([h0,-1*hoodSize]);
                        count.add([h0,hoodSize]);
                    }
                    for (var h1=-1*hoodSize+1; h1<hoodSize; h1++) {
                        count.add([-1*hoodSize,h1]);
                        count.add([hoodSize,h1]);
                    }
                    desc.push(count);
                }
                
                //var lines=[];
                for (dir of directions) {
                    var count=makePieceCounter();
                    var curPos=[this.i0+dir[0],this.i1+dir[1]];
                    while (curPos[0]>=0 && curPos[0]<board.length &&
                           curPos[1]>=0 && curPos[1]<board[curPos[0]].length) {
                        count.add(curPos);
                        curPos[0]+=dir[0];
                        curPos[1]+=dir[1];
                    }
                    desc.push(count);
                }
                //knight moves?
                //TODO have these defined by what move types are found in the hlgdl
                
                
                this.descAllOr.push(desc);
            }
        
        } else {
            console.log('ERROR, macro type '+hlgdl.board.macroType+' not implemented for Description.');
            this.descAllOr=null;
            return;
        }
    }
    Description.prototype.sqDistance = function(other) {
        if (this.player!==other.player) {
            return 99999; //don't compare pieces of other players
        }
        var minDist=999999;
        for (descThis of this.descAllOr) {
            for (descOther of other.descAllOr) {
                if (descThis.length==descOther.length) {
                    var dist=0;
                    for (var i=0; i<descThis.length; i++) {
                        dist+=descThis[i].sqDistance(descOther[i]);
                    }
                    if (dist<minDist)
                        minDist=dist;
                } else {
                    console.log("ERROR, descs are different lengths (this shouldn't happen)");
                    return null;
                }
            }
        }
        minDist *= this.piece===other.piece?1:1.5;
        
        //TODO, this is rather ill-formed, only works for square tile
        var boardSize;
        if (this.hlgdl.board.layoutShape==='Square') {
            boardSize=this.hlgdl.board.size;
        } else if (this.hlgdl.board.layoutShape==='Rectangle') {
            boardSize=Math.sqrt(this.hlgdl.board.size*this.hlgdl.board.size2);
        } else {
            console.log('ERROR, layoutShape '+this.hlgdl.board.layoutShape+' not implemented for Description');
            return null;
        }
        minDist *= Math.sqrt(Math.pow(descThis.i0-descOther.i0,2) + Math.pow(descThis.i1-descOther.i1,2))/boardSize;
        return minDist;
    }
    
    //This returns an array of the pieces' local descriptions
    function parseState(hlgdl,state) {
        var RE_cells = /\(CELL [0-9]+ [0-9]+ \w+\)/ig;
        var RE_cell =  /\(CELL ([0-9]+) ([0-9]+) (\w+)\)/i;
        var board=null;
        var ret=[];
        //TODO other board types
        if (hlgdl.board.macroType=='Grid') {
            if (hlgdl.board.tileType=='Square') {
                
                if (hlgdl.board.layoutShape=='Square') {
                    board=new Array(hlgdl.board.size);
                    for (var i = 0; i < hlgdl.board.size; i++) {
                        board[i] = new Array(hlgdl.board.size);
                    }
                } else if (hlgdl.board.layoutShape=='Rectangle') {
                    board=new Array(hlgdl.board.size);
                    for (var i = 0; i < hlgdl.board.size; i++) {
                        board[i] = new Array(hlgdl.board.size2);
                    }
                } else {
                    console.log('ERROR, unknown layoutShape: '+hlgdl.board.layoutShape);
                    return null;
                }
                
                
            }
            //console.log(state);
            var cellsStr = state.match(RE_cells);
            //console.log(cellsStr);
            for (s of cellsStr) {
                var m = s.match(RE_cell);
                var i0 = +(m[1])-1;
                var i1 = +(m[2])-1;
                var p = m[3].toLowerCase();
                if (p===undefined)
                    console.log('ERROR p==undefined, '+s);
                board[i0][i1]=p;
            }
            
            for (s of cellsStr) {
                var m = s.match(RE_cell);
                var i0 = (+m[1])-1;
                var i1 = (+m[2])-1;
                var p = m[3].toLowerCase();
                if (p != 'b')
                    ret.push(new Description(i0,i1,p,board,hlgdl));
            }
            
        }
        return ret;
    }
    
    function getStateDistanceMetric(hlgdl,params) {
        return function (a,b) {
            //a,b={returnObj:{}, stateParsed:null, state:'(...)', scores:[scores]}
            
            
            if (a.stateParsed==null)
                a.stateParsed=parseState(hlgdl,a.state);
            if (b.stateParsed==null)
                b.stateParsed=parseState(hlgdl,b.state);
            
            function findNearest(piece,otherPieces,matched,unmatched) {
                var minDist=null;
                for (var i=0; i<otherPieces.length; i++) {
                    var pieceB=otherPieces[i];
                    if (piece.player===pieceB.player) {
                        var dist = piece.sqDistance(pieceB);
                        if ((minDist==null||dist<minDist) && (matched[i]===undefined || matched[i].dist>dist)) {
                            minDist=dist;
                            var doAgain=null;
                            if (matched[i]!==undefined)
                                doAgain=matched[i].piece;
                            matched[i]={piece:piece, dist:dist};
                            if (doAgain!==null)
                                findNearest(doAgain,otherPieces,matched,unmatched);
                        }
                    }
                }
                if (minDist===null) {
                    unmatched.push(piece);
                }
            }
            
            function stateDistOneWay (piecesA, piecesB) {
                var ret=0;
                var matched={};
                var unmatched=[];
                for (var pieceA of piecesA) {
                    findNearest(pieceA,piecesB,matched,unmatched);
                }
                for (var m in matched) {
                    if (matched.hasOwnProperty(m))
                        ret += matched[m].dist;
                }
                
                //This gives a heavy penalty to having an uneven number of pieces
                //But given that the other direction won't have the penalty, it should work out.
                if (piecesB.length>0) {
                    for (u of unmatched) {
                        var sum=0;
                        var count=0;
                        for (pieceB of piecesB) {
                            if (u.player===pieceB.player) {
                                count++;
                                sum+=u.sqDistance(pieceB);
                            }
                        }
                        ret += sum/(0.0+count);
                    }
                }
                return ret;
            }
            
            var sumDist=stateDistOneWay(a.stateParsed,b.stateParsed) + stateDistOneWay(b.stateParsed,a.stateParsed);
            
            
            return Math.sqrt(sumDist + Math.pow(a.step-b.step,2)*params.stepDifWeight);
        };
    }
    
    function learnPositionStrength(hlgdl,matches,numPlayers,params) {
        var turnsScores=[];
        var avgNumTurnsPerMatch=0;
        
        for (var matchInfo of matches) {
            
            matchInfo.turnsStrengthScored=[]
            var step=0;
            for (turn of matchInfo.turns) {
                var toAdd={strengthScored:null};
                matchInfo.turnsStrengthScored.push(toAdd)
                turnsScores.push({
                                    returnObj:toAdd,
                                    step:step++,
                                    state:turn,
                                    stateParsed:null,
                                    scores:matchInfo.outcome
                                 });
            }
            avgNumTurnsPerMatch+=matchInfo.turns.length;
            
        }
        avgNumTurnsPerMatch/=0.0+matches.length;
        
        var levels = cluster({
            input: turnsScores,
            distance: getStateDistanceMetric(hlgdl,params),
            linkage: function (distances) {
                //return Math.min.apply(null, distances);//single-link (min) clustering
                var sum=0;//average-link
                for (d of distances)
                    sum+=d;
                return sum/(0.0+distances.length);
            },
            minClusters: params.clusteringKCoef*avgNumTurnsPerMatch
        });
        
        
        var clusters = levels[levels.length - 1].clusters;
        for (var clust of clusters) {
            var accumScore= new Array(numPlayers);//Array.apply(null, Array(numPlayers)).map(Number.prototype.valueOf,0);
            for (var i=0; i<numPlayers; i++)
                accumScore[i]=0;
            
            for (index of clust) {
                for (var i=0; i<numPlayers; i++) {
                    accumScore[i]+=+turnsScores[index].scores[i];
                    
                }
            }
            for (var i=0; i<numPlayers; i++)//normalize
                accumScore[i]/=0.0+clust.length;
            
            //We return the clustering-scoring by setting the value of an object shared with an array by the matchInfo object
            for (index of clust) {
                turnsScores[index].returnObj.strengthScored=accumScore;
            }
        }
        //return clusters;
    }
    
    return learnPositionStrength;
};
    
    

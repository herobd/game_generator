module.exports = function() {

    var cluster = require('hierarchical-clustering');
    
    
    
    function createDescription(i0,i1,p,board,hlgdl) {
        var RE_piece = /(\w+)_(\w+)/;
        var thisPlayer = p.match(RE_piece)[1];
        
        function getPiece(dir) {
            if (board.length>i0+dir[0] && i0+dir[0]>=0 &&
                board[i0+dir[0]].length>i1+dir[1] && i1+dir[1]>=0) {
                var m= board[i0+dir[0]][i1+dir[1]].match(RE_piece);
                if (m!=null)
                    return m[2];
            }
            return null;
        }
        function getPlayer(dir) {
            if (board.length>i0+dir[0] && i0+dir[0]>=0 &&
                board[i0+dir[0]].length>i1+dir[1] && i1+dir[1]>=0) {
                var m= board[i0+dir[0]][i1+dir[1]].match(RE_piece);
                if (m!=null)
                    return m[1];
            }
            return null;
        }
        
        
        
        var orientations = [[[0,-1], [0, 1], [-1,0], [ 1,0],[-1,-1], [-1, 1], [ 1,-1], [ 1, 1]},
                            [[0, 1], [0,-1], [-1,0], [ 1,0],[-1, 1], [-1,-1], [ 1, 1], [ 1,-1]},
                            [[0,-1], [0, 1], [ 1,0], [-1,0],[ 1,-1], [ 1, 1], [-1,-1], [-1, 1]},
                            [[0, 1], [0,-1], [ 1,0], [-1,0],[ 1, 1], [ 1,-1], [-1, 1], [-1,-1]}];
        var revOrientations=[]
        for (var directions of orientations) {
            var revDir=[]
            for (dir of directions) {
                revDir.push([dir[1],dir[0]]);
            }
            revOrientations.push(revDir);
        }
        orientations=orientations.concat(revOrientations);
        if (hlgdl.board.macroType=='Grid') {
            if (hlgdl.board.tileType=='Square') {
                //desc:n,s,w,e,nw,ne,sw,se,count_p1,count_p2,count_p3,count_n,count_s,count_w,count_e,count_nw,count_ne,count_sw,count_se,count_knight
                descAllOr=[];
                for (var directions of orientations) {
                    
                
                    var descNeighbors=[];
                    for (dir of directions) {
                        if (getPlayer(dir)==thisPlayer){
                            descNeighbors.push(getPiece(dir));
                            descNeighbors.push('');
                        } else if (getPlayer(dir)!=null){
                            descNeighbors.push('');
                            descNeighbors.push(getPiece(dir));
                            
                        } else {
                            descNeighbors.push('');
                            descNeighbors.push('');
                        }
                    }
                    
                    var hoods=[];
                    function countPieceTypes (count,pos) {
                        var toAdd;
                        var pp=getPlayer(pos);
                        if (pp!=null){
                            if (pp==thisPlayer)
                                toAdd=count.allies;
                            else
                                toAdd=count.enemies;
                            var pieceType = getPiece(pos);
                            if (toAdd.hasOwnProperty(pieceType))
                                toAdd[pieceType]+=1;
                            else
                                toAdd[pieceType]=1;
                        }
                    }
                    for (var hoodSize=1; hoodSize<=3; hoodSize++) {
                        var count={allies:{}, enemies:{}};
                        for (var h0=-1*hoodSize; h0<=hoodSize; h0++) {
                            
                            countPieceTypes(count,[h0,-1*hoodSize]);
                            countPieceTypes(count,[h0,hoodSize]);
                        }
                        for (var h1=-1*hoodSize+1; h1<hoodSize; h1++) {
                            countPieceTypes(count,[-1*hoodSize,h1]);
                            countPieceTypes(count,[hoodSize,h1]);
                        }
                        hoods.push(count);
                    }
                    
                    var lines=[];
                    for (dir of directions) {
                        var count={allies:{}, enemies:{}};
                        var curPos=[i0+dir[0],i1+dir[1]];
                        while (curPos[0]>=0 && curPos[0]<board.length &&
                               curPos[1]>=0 && curPos[1]<board[curPos[0].length) {
                            countPieceTypes(count,curPos);
                            curPos[0]+=dir[0];
                            curPos[1]+=dir[1];
                        }
                        lines.push(count);
                    }
                    
                    descAllOr.push({
                                neighbors:descNeighbors,
                                hoods:hoods,
                                lines:lines
                             });
                }
            }
        
        }
    }
    
    //This returns an array of the pieces' local descriptions
    function parseState(hlgdl,state) {
        var RE_cells = /\(CELL ([0-9]+) ([0-9]+) (\w+)\)/g;
        var RE_cell =  /\(CELL ([0-9]+) ([0-9]+) (\w+)\)/;
        var board=null;
        var ret={   pieces:[],
                    state:state.match(RE_state)[1],
                    step:state.match(RE_step)[1]
                };
        //TODO other board types
        if (hlgdl.board.macroType=='Grid') {
            if (hlgdl.board.tileType=='Square') {
                
                if (hlgdl.board.layoutShape=='Square') {
                    board=new Array(gameType.size);
                    for (var i = 0; i < gameType.size; i++) {
                        board[i] = new Array(gameType.size);
                    }
                } else if (hlgdl.board.layoutShape=='Rectangle') {
                    board=new Array(gameType.size);
                    for (var i = 0; i < gameType.size; i++) {
                        board[i] = new Array(gameType.size2);
                    }
                } else {
                    console.log('ERROR, unknown layoutShape: '+hlgdl.board.layoutShape);
                    return null;
                }
                
                
            }
            
            var cellsStr = state.match(RE_cells);
            for (s of cellsStr) {
                var m = s.match(RE_cell);
                var i0 = +(m[1]);
                var i1 = +(m[2]);
                var p = m[3];
                board[i0][i1]=p;
            }
            
            for (s of cellsStr) {
                var m = s.match(RE_cell);
                var i0 = +(m[1]);
                var i1 = +(m[2]);
                var p = m[3];
                ret.pieces.push(createDescription(i0,i1,p,board,hlgdl));
            }
            
        }
        return ret.pieces;
    }
    
    function getStateDistanceMetric(hlgdl,params) {
        return function (a,b) {
            //a,b={returnObj:{}, stateParsed:null, state:'(...)', scores:[scores]}
            
            
            if (a.stateParsed==null)
                a.stateParsed=parseState(boardType,a.state);
            if (b.stateParsed==null)
                b.stateParsed=parseState(boardType,b.state);
            
            var sumDist=0;
            for (pieceA of a.stateParsed.pieces) {
                for (pieceB of b.stateParsed.pieces) {
                    var minDist=999999;
                    for (descA of pieceA) {
                        for (descB of pieceB) {
                            var dist = descriptionDistance(descA,descB,params);
                            if (dist<minDist)
                                minDist=dist;
                        }
                    }
                    sumDist += minDist;
                }
            }
            
            return sumDist + ((a.stateParsed.state!=b.stateParsed.state)?params.stateDifDist:0) + 
        };
    }
    
    function learnPositionStrength(turnsScores,hlgdl,matches,numPlayers,params) {
        var turnsScores=[];
        var avgNumTurnsPerMatch=0;
        
        for (var matchInfo of matches) {
            
            matchInfo.turnsStrengthScored=[]
            for (turn of matchInfo.turns) {
                var toAdd={strengthScored:null};
                matchInfo.turnsStrengthScored.push(toAdd)
                turnsScores.push({
                                    returnObj:toAdd,
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
            minClusters: params.clusteringKCoef*avgNumTurnsPerGame
        });
        var clusters = levels[levels.length - 1].clusters;
        for (cluster of clusters) {
            var accumScore=Array.apply(null, Array(numPlayers)).map(Number.prototype.valueOf,0);
            for (index of cluster) {
                for (var i=0; i<numPlayers; i++) {
                    accumScore[i]+=turnsScores[index].scores[i]
                }
            }
            for (var i=0; i<numPlayers; i++)//normalize
                accumScore[i]/=0.0+cluster.length;
                
            //We return the clustering-scoring by setting the value of an object shared with an array by the matchInfo object
            for (index of cluster) {
                turnsScores[index].returnObj.strengthScored=accumScore;
            }
        }
        //return clusters;
    }
    
    return learnPositionStrength;
}
    
    

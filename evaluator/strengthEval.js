module.exports = function() {

    var cluster = require('hierarchical-clustering');
    
    function createDescription(i1,i2,p,board,hlgdl) {
        if (hlgdl.board.macroType=='Grid') {
            if (hlgdl.board.tileType=='Square') {
                
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
                
                if (hlgdl.board.layout=='Square') {
                    board=new Array(gameType.size);
                    for (var i = 0; i < gameType.size; i++) {
                        board[i] = new Array(gameType.size);
                    }
                } else if (hlgdl.board.layout=='Rectangle') {
                    board=new Array(gameType.size);
                    for (var i = 0; i < gameType.size; i++) {
                        board[i] = new Array(gameType.size2);
                    }
                } else {
                    console.log('ERROR, unknown layout: '+hlgdl.board.layout);
                    return null;
                }
                
                
            }
            
            var cellsStr = state.match(RE_cells);
            for (s of cellsStr) {
                var m = s.match(RE_cell);
                var i1 = +(m[1]);
                var i2 = +(m[2]);
                var p = m[3];
                board[i1][i2]=p;
            }
            
            for (s of cellsStr) {
                var m = s.match(RE_cell);
                var i1 = +(m[1]);
                var i2 = +(m[2]);
                var p = m[3];
                ret.pieces.push(createDescription(i1,i2,p,board,hlgdl));
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
    
    

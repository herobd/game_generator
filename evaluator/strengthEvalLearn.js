module.exports = function() {

    var C45 = require('c4.5');
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
    
    function PieceDescription(i0,i1,p,board,hlgdl) {
        
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
                console.log('ERROR, tile type '+hlgdl.board.tileType+' not implemented for PieceDescription.');
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
                    if (dir[0]!=0 || dir[1]!=0)
                        while (curPos[0]>=0 && curPos[0]<board.length &&
                               curPos[1]>=0 && curPos[1]<board[curPos[0]].length) {
                            count.add(curPos);
                            curPos[0]+=dir[0];
                            curPos[1]+=dir[1];
                        }
                    else
                        count.add(curPos);
                    desc.push(count);
                }
                //knight moves?
                //TODO have these defined by what move types are found in the hlgdl
                
                
                this.descAllOr.push(desc);
            }
        
        } else {
            console.log('ERROR, macro type '+hlgdl.board.macroType+' not implemented for PieceDescription.');
            this.descAllOr=null;
            return;
        }
    }
    PieceDescription.prototype.sqDistance = function(other) {
        //if (this.player!==other.player) {
        //    return 99999; //don't compare pieces of other players
        //}
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
            console.log('ERROR, layoutShape '+this.hlgdl.board.layoutShape+' not implemented for PieceDescription');
            return null;
        }
        minDist += Math.sqrt(Math.pow(descThis.i0-descOther.i0,2) + Math.pow(descThis.i1-descOther.i1,2))/boardSize;
        return minDist;
    }
    
    //This returns an array of the pieces' local descriptions
    function StateDescription(hlgdl,state,allPieces) {
        var RE_cells = /\(CELL [0-9]+ [0-9]+ \w+\)/ig;
        var RE_cell =  /\(CELL ([0-9]+) ([0-9]+) (\w+)\)/i;
        var board=null;
        this.pieces=[];
        //TODO other board types
        if (hlgdl.board.macroType=='Grid') {
            if (hlgdl.board.tileType=='Square') {
                
                if (hlgdl.board.layoutShape=='Square') {
                    board=new Array(hlgdl.board.size);
                    for (var i = 0; i < hlgdl.board.size; i++) {
                        board[i] = new Array(hlgdl.board.size);
                        for (var j = 0; j < hlgdl.board.size; j++)
                            board[i][j]='';
                    }
                } else if (hlgdl.board.layoutShape=='Rectangle') {
                    board=new Array(hlgdl.board.size);
                    for (var i = 0; i < hlgdl.board.size; i++) {
                        board[i] = new Array(hlgdl.board.size2);
                        for (var j = 0; j < hlgdl.board.size2; j++)
                            board[i][j]='';
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
                if (p != 'b') {
                    var piece = new PieceDescription(i0,i1,p,board,hlgdl);
                    this.pieces.push(piece);
                    allPieces.push(piece);
                }
            }
            
        }
        
        
        
        //return ret;
        this.array=null;
    }
    
    StateDescription.prototype.toArray = function(players,pieceConfigurationClassifier) {
        if (this.array==null) {
            this.array = [];
            for (var i=0; i<pieceConfigurationClassifier.numClasses*players.length; i++) {
                this.array.push(0);
            }
            for (var piece of this.pieces) {
                var cls = pieceConfigurationClassifier.classify(piece);
                this.array[cls+players.indexOf(piece.player)*pieceConfigurationClassifier.numClasses]++;
            }
        }
        return this.array;
    }
    
    function learnPieceConfigurations(numOfPieceTypes,pieces) {
        var levels = cluster({
            input: pieces,
            distance: function(a,b){return a.sqDistance(b);},
            linkage: function (distances) {
                //return Math.min.apply(null, distances);//single-link (min) clustering
                var sum=0;//average-link
                for (d of distances)
                    sum+=d;
                return sum/(0.0+distances.length);
            },
            minClusters: Math.floor(4*numOfPieceTypes) //TODO magic number 4
        });
        
        var clusters = levels[levels.length - 1].clusters;
        clusters = clusters.map(function (cluster) {
          return cluster.map(function (index) {
            return pieces[index];
          });
        });
        return {
                    clusters:clusters,
                    classify: function(piece) {
                        var minDist=null;
                        var minClust=null;
                        for (var i=0; i<this.clusters.length; i++) {
                            var dist=0;
                            for (var cp of this.clusters[i]) {
                                dist+=piece.sqDistance(cp);
                            }
                            dist /= this.clusters[i].length;
                            if (dist<minDist) {
                                minDist=dist;
                                minClust=i;
                            }
                        }
                        return minClust;
                    },
                    numClasses:clusters.length
        }
    }
    
    
    function shuffle(a,b) {
        var j, x, i;
        for (i = a.length; i; i -= 1) {
            j = Math.floor(Math.random() * i);
            x = a[i - 1];
            x = a[i - 1];
            a[i - 1] = a[j];
            a[j] = x;
            if (b!==undefined) {
                x = b[i - 1];
                x = b[i - 1];
                b[i - 1] = b[j];
                b[j] = x;
            }
        }
    }
    function learnPositionStrength(hlgdl,matches,numPlayers,params,callback) {
        //console.log('Learning pos str');
        var turnsScores=[];
        var allPieces=[];
        var avgNumTurnsPerMatch=0;
        var maxInstances=1000;
        for (var matchInfo of matches) {
            if (maxInstances-- < 0)
                break;
            
            matchInfo.turnsStrengthScored=[]
            var step=0;
            for (turn of matchInfo.turns) {
                var toAdd={strengthScored:null};
                matchInfo.turnsStrengthScored.push(toAdd)
                var desc=new StateDescription(hlgdl,turn,allPieces);
                turnsScores.push({
                                    returnObj:toAdd,
                                    step:step++,
                                    state:turn,
                                    stateParsed:desc,
                                    scores:matchInfo.outcome
                                 });
                //var feats = desc.toArray();
                //feats.push(matchInfo.outcome.join(' '));
                //allData.push(feats);
            }
            avgNumTurnsPerMatch+=matchInfo.turns.length;
            
        }
        avgNumTurnsPerMatch/=0.0+matches.length;
        
        shuffle(allPieces);
        var pieceConfigurationClassifier = learnPieceConfigurations(hlgdl.pieces.length,allPieces.slice(0,200));
        var allData=[];
        for (var turnScore of turnsScores) {
            var feats = turnScore.stateParsed.toArray(hlgdl.players,pieceConfigurationClassifier);
            feats.push(turnScore.scores.join(' '));
            allData.push(feats);
            //console.log(feats.join())
        }
        
        shuffle(allData,turnsScores);
        
        var splitPoint = Math.max(200,allData.length/2);
        var trainingData = allData.slice(0,splitPoint);
        
        var c45 = C45();
        var featuresList=[];
        var featureTypes=[];
        for (var i=0; i<pieceConfigurationClassifier.numClasses*numPlayers; i++) {
            featuresList.push('attr'+i);
            featureTypes.push('number');
        }
        c45.train(  {
                        data: trainingData,
                        target: 'class',
                        features: featuresList,
                        featureTypes: featureTypes
                    },
                    function(error, model) {
                        if (error) {
                            console.error(error);
                            return false;
                        }
                        
                        //Change tree; we give it our own labels on each leaf node
                        var tagger={tags:0, next:function(){return this.tags++}};
                        function traverseTag(tagger,node) {
                            if (node.type=='result') {
                                node.value=tagger.next()
                            } else {
                                for (var n in node.values)
                                    if (node.values.hasOwnProperty(n))
                                        traverseTag(tagger,node.values[n].child);
                            }
                        }
                        traverseTag(tagger,model.model);
                        
                        //We then evalute what is "collected" at each leaf. (the average scores)
                        var accumScores = new Array(tagger.tags);
                        var accumScoresCount = new Array(tagger.tags);
                        for (var i=0; i<allData.length; i++) {
                            var tag = model.classify(allData[i]);
                            turnsScores[i].tag=tag;
                            //console.log(turnsScores[i].state+' with scores:'+turnsScores[i].scores.join()+' given tag: '+tag);
                            if (accumScores[tag]==undefined || accumScores[tag]==null) {
                                accumScores[tag]=turnsScores[i].scores.slice();
                                accumScoresCount[tag]=1;
                            } else {
                                for (var si=0; si<turnsScores[i].scores.length; si++) {
                                    accumScores[tag][si]+=turnsScores[i].scores[si];
                                }
                                accumScoresCount[tag]++;
                            }
                        }
                        
                        for (var tag=0; tag<tagger.tags; tag++) {
                            if (accumScores[tag]!==undefined)
                                for (var si=0; si<accumScores[tag].length; si++) {
                                    accumScores[tag][si]/=0.0+accumScoresCount[tag];
                                }
                        }
                        
                        //Use the average collected scores as the predicted strength
                        for (var i=0; i<allData.length; i++) {
                            
                            turnsScores[i].returnObj.strengthScored=accumScores[turnsScores[i].tag];
                        }
                        
                        
                        callback();
                    }
        );
        
    }
    
    return learnPositionStrength;
};
    
    
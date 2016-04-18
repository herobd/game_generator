module.exports = function() {

    var MLP = require('mlp');
    //var brain = require('brain');
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
        if (isNaN(this.i0) || isNaN(this.i1))
            console.log('Nan: '+i0+', '+i1)
    }
    pieceCounter.prototype.add = function(pos) {
        //console.log('add '+pos[0]+' '+pos[1]);
        pos[0]=+pos[0]
        pos[1]=+pos[1]
        var pp=this.getPlayer(pos);
        
        if (pp!=null){
            //console.log('add for player '+pp)
            
            var pieceType = this.getPiece(pos);
            if (this.players[pp].hasOwnProperty(pieceType))
                this.players[pp][pieceType]+=1;
            else
                this.players[pp][pieceType]=1;
            this.players[pp].total+=1;
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
            //console.log('no match for '+p)
        }
        //console.log((this.i0+dir[0]) +' , '+(this.i1+dir[1]))
        //console.log(this.board.length +' x '+this.board[0].length)
        return null;
    }
    pieceCounter.prototype.sqDistancePart = function(countedA,countedB) {
        var ret=0;
        for (var prop in countedA) {
            
            if (countedA.hasOwnProperty(prop)) {
                //console.log('prop '+prop+' for A')
                if (countedB.hasOwnProperty(prop))
                    ret += Math.pow(countedA[prop]-countedB[prop],2);
                else
                    ret += Math.pow(countedA[prop],2);
            }
        }
        for (var prop in countedB) {
            if (countedB.hasOwnProperty(prop)) {
                //console.log('prop '+prop+' for B')
                if (!countedA.hasOwnProperty(prop))
                    ret += Math.pow(countedB[prop],2);
            }
        }
        if (isNaN(ret))
            console.log('ERROR, NaN in sqDistancePart')
        return ret;
    }
    pieceCounter.prototype.sqDistance = function(other) {
        if (this.player!==other.player) {
            console.log('not matching players: '+this.player+', '+other.player);
            return 0;
        }
        
        var ret=0;
        //ret+=this.sqDistancePart(this.allies,other.allies);
        //ret+=this.sqDistancePart(this.enemies,other.enemies);
        //console.log('counter there are '+this.players.length+' players')
        for (var pname in this.players) {
            if (this.players.hasOwnProperty(pname)) {
                ret+=this.sqDistancePart(this.players[pname],other.players[pname]);
                //console.log('counter dist for player '+pname+': '+this.sqDistancePart(this.players[pname],other.players[pname]));
            }
        }
        
        return ret;
    }
    
    pieceCounter.prototype.getSparse = function(i,sparse) {
        for (var pname in this.players) {
            if (this.players.hasOwnProperty(pname)) {
                for (var prop in this.players[pname]) {
                    
                    if (this.players[pname].hasOwnProperty(prop)) {
                        if (this.players[pname][prop] > 0) {
                            sparse[i]=this.players[pname][prop];
                        }
                        i++;
                    }
                }
            }
        }
        return i;
    }
    
    function PieceDescription(i0,i1,p,board,hlgdl) {
        if (isNaN(i0) || isNaN(i1))
                    console.log('PD, '+i0+', '+i1)
        this.hlgdl=hlgdl;
        this.i0=+i0;
        this.i1=+i1;
        this.sparse=null;
        var RE_piece = /(\w+)_(\w+)/;
        //console.log(p);
        this.player = p.match(RE_piece)[1];
        this.piece = p.match(RE_piece)[2];
        function makePieceCounter() {
            return new pieceCounter(+i0,+i1,board,hlgdl.players);
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
            
            //console.log('should be adding desc for orientations '+orientations.length)
            for (var directions of orientations) {
                
            
                var desc=[];
                //console.log('should be adding directions '+directions.length)
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
                    var curPos=[(+i0)+dir[0],(+i1)+dir[1]];
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
            
            this.makeSparse();
        
        } else {
            console.log('ERROR, macro type '+hlgdl.board.macroType+' not implemented for PieceDescription.');
            this.descAllOr=null;
            return;
        }
    }
    
    PieceDescription.prototype.makeSparse = function() {
        this.sparse = [];
        for (var desc of this.descAllOr) {
            var sparse = []
            var i = 0;
            for (var piece of desc)
                i += piece.getSparse(i,sparse);
        }
    }
    
    PieceDescription.prototype.distance = function(other) {
        //if (this.player!==other.player) {
        //    return 99999; //don't compare pieces of other players
        //}
        var minDist=null;       
        
        //console.log('sqd there are '+this.descAllOr.length+' orientations')
        for (var descThis of this.sparse) {
            for (var descOther of other.sparse) {
                if (descThis.length==descOther.length) {
                    var dist=0;
                    //console.log('sqd there are '+descThis.length+' counters')
                    //for (var i=0; i<descThis.length; i++) {
                    //    dist+=descThis[i].sqDistance(descOther[i]);
                    //}
                    for (var index in descThis) {
                        if (descThis.hasOwnProperty(index)) {
                            if (descOther.hasOwnProperty(index))
                                dist+=Math.abs(descThis[index]-descOther[index]);
                            else
                                dist+=descThis[index];
                        }
                    }
                    for (var index in descOther) {
                        if (descOther.hasOwnProperty(index)) {
                            if (!descThis.hasOwnProperty(index))
                                dist+=descOther[index];
                        }
                    }
                    
                    
                    if (dist<minDist || minDist==null)
                        minDist=dist;
                } else {
                    console.log("ERROR, descs are different lengths (this shouldn't happen)");
                    return null;
                }
            }
        }
        minDist *= this.piece===other.piece?1:3;
        
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
        minDist += Math.sqrt(Math.pow(this.i0-other.i0,2) + Math.pow(this.i1-other.i1,2))/boardSize;
        if (isNaN(minDist))
            console.log('ERROR, isNan sqDist 2  board:'+boardSize+' descOther:'+other.i0+','+other.i1+'  descThis:'+this.i0+','+this.i1)
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
                if (isNaN(i0) || isNaN(i1))
                    console.log('cell: s: '+s+', '+i0+', '+i1)
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
            distance: function(a,b){return a.distance(b);},
            linkage: function (distances) {
                //return Math.min.apply(null, distances);//single-link (min) clustering
                var sum=0;//average-link
                for (var d of distances)
                    sum+=d;
                //console.log('di '+ (sum/(0.0+distances.length)));
                return sum/(0.0+distances.length);
            },
            minClusters: Math.floor(4*numOfPieceTypes) //TODO magic number 4
        });
        
        var clusters = levels[levels.length - 1].clusters;
        //console.log(clusters.join())
        //console.log(levels[0].clusters.join())
        clusters = clusters.map(function (cluster) {
          return cluster.map(function (index) {
            return pieces[index];
          });
        });
        /*for (var c of clusters) {
            console.log('{')
            for (var i of c) {
                var bb='';
                for (var x in c)
                    if (c.hasOwnProperty(x))
                        bb+=c[x]+','
                console.log('  ['+bb+']')            
            }
            console.log('}')
        }*/
        //console.log('There are '+clusters.length+' clusters from '+pieces.length+' instances')
        //console.log('There should be '+(4*numOfPieceTypes)+' clusters. There are '+levels.length+' levels')
        return {
                    clusters:clusters,
                    classify: function(piece) {
                        var minDist=null;
                        var minClust=null;
                        
                        //var minDist2=null;
                        //var minClust2=null;
                        for (var i=0; i<this.clusters.length; i++) {
                            var dist=null;
                            for (var cp of this.clusters[i]) {
                                var tmpDist=piece.distance(cp);
                                if (tmpDist<dist || dist==null)
                                    dist=tmpDist;
                            }
                            if (dist<minDist || minDist==null) {
                                //minDist2=minDist;
                                //minClust2=minClust;
                            
                                minDist=dist;
                                minClust=i;
                            } //else if (dist<minDist2 || minDist2==null) {
                            //    minDist2=dist;
                            //    minClust2=i;
                            //}
                        }
                        //console.log ('this piece looks like '+minClust+' with dist '+minDist)
                        //console.log ('runner up is '+minClust2+' with dist '+minDist2)
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
        var maxScore=0;
        var minScore=0;
        var matchNum=0;
        for (var matchInfo of matches) {
            if (maxInstances-- < 0)
                break;
            
            matchInfo.turnsStrengthScored=new Array(matchInfo.turns.length)
            var step=0;
            for (var turn of matchInfo.turns) {
                //var toAdd={strengthScored:null};
                //matchInfo.turnsStrengthScored.push(toAdd)
                var desc=new StateDescription(hlgdl,turn,allPieces);
                for (var score of matchInfo.outcome) {
                    if (score>maxScore)
                        maxScore=score;
                    if (score<minScore)
                        minScore=score;
                }
                
                turnsScores.push({
                                    //returnObj:toAdd,
                                    step:step++,
                                    matchNum:matchNum,
                                    state:turn,
                                    stateParsed:desc,
                                    scores:matchInfo.outcome
                                 });
                //var feats = desc.toArray();
                //feats.push(matchInfo.outcome.join(' '));
                //allData.push(feats);
            }
            avgNumTurnsPerMatch+=matchInfo.turns.length;
            matchNum++;
        }
        avgNumTurnsPerMatch/=0.0+matches.length;
        
        shuffle(allPieces);
        var pieceConfigurationClassifier = learnPieceConfigurations(hlgdl.pieces.length,allPieces.slice(0,200));
        var featDim=pieceConfigurationClassifier.numClasses*numPlayers;
        var minValues=new Array(featDim);
        var maxValues=new Array(featDim);
        var trainingData=[];
        for (var turnScore of turnsScores) {
            var feats = turnScore.stateParsed.toArray(hlgdl.players,pieceConfigurationClassifier);
            var normalizedScores = turnScore.scores.slice();
            for (var i=0; i<normalizedScores.length; i++)
                normalizedScores[i] = (normalizedScores[i]-minScore+0.0)/maxScore;
            trainingData.push({input:feats, output:normalizedScores});
            for (var i=0; i<feats.length; i++) {
                if (feats[i]>maxValues[i] || maxValues[i]==undefined ||maxValues[i]==null)
                    maxValues[i]=feats[i];
                if (feats[i]<minValues[i] || minValues[i]==undefined ||minValues[i]==null)
                    minValues[i]=feats[i];
            }
            //feats.push(turnScore.scores.join(' '));
            //allData.push(feats);
            //	console.log(feats.join())
        }
        //console.log('num dim '+pieceConfigurationClassifier.numClasses*numPlayers)
        
        //shuffle(allData,turnsScores);
        
        //var splitPoint = Math.max(allData.length/2);
        //var trainingData = allData.slice(0,splitPoint);
        
        //normalize features
        for (var datum of trainingData) {
            for (var i=0; i<datum.input.length; i++) {
                if (maxValues[i]!=0)
                    datum.input[i] = (datum.input[i]-minValues[i]+0.0)/maxValues[i];
            }
            //console.log ('['+datum.input.join()+'] : {'+datum.output.join()+'}')
        }
        
        var mlp = new MLP(featDim,numPlayers);
        mlp.addHiddenLayer(featDim*2);
        mlp.init();
        
        //shuffle(trainingData,turnsScores);
        for (var inst of trainingData) {
            mlp.addToTrainingSet(inst.input, inst.output);
        }
        
        // train the perceptron
        var learnRate = 0.001;
        var error = Number.MAX_VALUE;
        var lastError = Number.MAX_VALUE;
        var inARow = false;
        for (var i=0; i<4000&&error > 0.1; i++) {
            if (i==1000)
                learnRate/=5;
	        error = mlp.train(learnRate);
	        //if (i%250==0)
	            //console.log (i+': '+error);
	            
            if (lastError-error<0.0001 && lastError>error && i>500) {
                if (inARow)
                    break;
                else
                    inARow=true;
            }
            else
                inARow=false;
            lastError=error;
        }
        //console.log("trained with error: "
        //                + error);
        
        
        for (var i=0; i<turnsScores.length; i++) {
            var res = mlp.classify(trainingData[i].input);
            //turnsScores[i].returnObj.strengthScored=res;
            matches[turnsScores[i].matchNum].turnsStrengthScored[turnsScores[i].step]={strengthScored:res.slice()};
            //console.log ('['+trainingData[i].input.join()+'] > {'+res.join()+'}')
        }
        
        
        callback();

        /*var net = new brain.NeuralNetwork({
          hiddenLayers: [pieceConfigurationClassifier.numClasses*numPlayers*2],
          learningRate: 0.1 // global learning rate, useful when training using streams 
        });
        var trainStream = net.createTrainStream({
          floodCallback: function() {
            shuffle(trainingData,turnsScores);
            flood(trainStream, trainingData);
          },

          
          doneTrainingCallback: function(obj) {
            console.log("trained in " + obj.iterations + " iterations with error: "
                        + obj.error);

            
            for (var i=0; i<turnsScores.length; i++) {
                
                turnsScores[i].returnObj.strengthScored=net.run(trainingData[i].input);
            }
            
            
            callback();

          },
          
          errorThresh: 0.05,  // error threshold to reach 
          iterations: 10000,   // maximum training iterations
          learningRate: 0.1    // learning rate
        });

        // kick it off
        flood(trainStream, trainingData);


        function flood(stream, data) {
          for (var i = 0; i < data.length; i++) {
            stream.write(data[i]);
          }
          // let it know we've reached the end of the data
          stream.write(null);
        }*/
        
        
        
        
    }
    
    return learnPositionStrength;
};
    
    

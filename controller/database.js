module.exports =  function() {
    
   var fs = require('fs');
    function Database(address,gdlDir, callback) {
        //TODO...
        // Retrieve
        var self=this;
        if (gdlDir===undefined)
            self.gdlDir='./gdl';
        else
            self.gdlDir=gdlDir;
        self.writing=[];
        
        fs.access(self.gdlDir,fs.F_OK, function(err) {
            if (err) {
                fs.mkdir(self.gdlDir,function(){});
            } //else {
            //    fs.unlink(self.gdlDir+'/*.gdl',function(){});
            //}
        });
        
        
        
        self.mongo = require('mongodb').MongoClient;

        // Connect to the db (localhost:27017/exampleDb)
        self.mongo.connect("mongodb://"+address, function(err, db) {
          if(!err) {
            console.log("We are connected to the database.");
            db.collection('GAMES', function(err, collection) {
                if(!err) {
                    self.gameCollection=collection;
                    callback();
                } else {
                    console.log('ERROR: conencting to MongoDB colection GAMES: '+err);
                }
            });
            
          } else {
            console.log('ERROR: conencting to MongoDB: '+err);
          }
        });
        this.test='ok';
    }
    
    Database.prototype.storeGame = function (meta,gdl,hlgdl,callback)  {
        
        var self=this;
        meta.isGame=1;
        function finish() {
            self.gameCollection.insert(meta, {w:1}, function(err, result) {
                if (err) {
                    callback(err);
                } else
                    self.gameCollection.update({id:meta.id}, {$set:{gdl:gdl, hlgdl:hlgdl}},{w:1}, function(err, result) {
                        if (err)
                            callback(err);
                        else
                            callback('ok');
                    });
            });
        }
        //Be sure we don't overlap
        self.gameCollection.deleteMany({id:meta.id, isGame:1}, {w:1}, function(err, result) {
            if (err) {
                callback(err);
            } else {
                
                if (JSON.parse(result).n>0) {
                    var filePath = self.gdlDir+'/'+meta.id+'.gdl';
                    fs.access(filePath,fs.R_OK, function(err) {
                        if(!err) {
                            console.log('gdl file already exists. Deleting it.');
                            fs.unlink(filePath,function(err){
                                if (err) {
                                    console.log('Could not remove prev existing gdl file '+filePath+': '+err);
                                    callback(err);
                                } else
                                    finish();
                            });
                        } else
                            finish();
                    });
                } else
                    finish();
                
            }
        });
        //console.log('Database is stubbed. Would be storing game '+meta.id+': '+meta.name);
        
    };
    
    /*Database.prototype.gdl = function (gameId,callback) {
        console.log('Database is stubbed. Would be retrieving gdl for '+gameId);
        callback('(role none)');
    }*/
    
    Database.prototype.getMetaForEval = function (gameId,callback) {
        this.gameCollection.findOne({id:gameId, isGame:1},{id:1,gdl:1,hlgdl:1,numPlayers:1}, function(err, item) {
            callback(err,item);//this returns an extra '_id' field, but oh well
        });
    };
    
    Database.prototype.gdlFileLocation = function (gameId,callback) {
        //console.log('Database is stubbed. Would be retrieving gdl file location for '+gameId);
        //if it doesnt exist, create it
        var self=this;
        var filePath = self.gdlDir+'/'+gameId+'.gdl';
        fs.access(filePath,fs.R_OK, function(err) {
            if (err) {
                if (self.writing[filePath]===undefined) {
                    self.writing[filePath]=true;
                    self.gameCollection.findOne({id:gameId}, function(err, item) {
                        fs.writeFile(filePath, item.gdl, function() {
                            self.writing[filePath]=false;
                            callback(filePath);
                        });
                    });
                } else if (self.writing[filePath]===true){
                    setTimeout(function() {//wait until we're done writting the file
                        self.gdlFileLocation(gameId, function(loc) {
                            callback(loc);
                        });
                    }, 500);
                } else {
                    callback(filePath);
                }
                
            } else {
                callback(filePath);
            }
        });
        
        
        //if (gameId===0)
        //callback('./gamecontroller/ticTacToe.kif');
        //else
        //    callback('./example.kif');
    };
    
    Database.prototype.storeScore = function (gameId,score,callback) {
        //console.log('Database is stubbed. Soring score for '+gameId);
        this.gameCollection.update({id:gameId, isGame:1}, {$set:{score:score}},{w:1}, function(err, result) {
            if (err)
                callback(err);
            else
                callback('ok');
        });
    }
    
    Database.prototype.storeGameResults = function (results,callback) {
        //console.log('Database is stubbed. Soring game results for game: '+results.gameId);
        this.gameCollection.update({id:results.gameId, isGame:1}, {$push:{match_simulation_results:results}},{w:1}, function(err, result) {
            if (err)
                callback(err);
            else
                callback('ok');
        });
    };
    
    Database.prototype.getMaxMatchId = function (callback) {
        console.log('Database is stubbed. Would be retrieving max match id');
        //if it doesnt exist, create it
        callback(0);
    };
    
    Database.prototype.getLastParams = function (componentId,callback) {
        console.log('Database is stubbed. Would be retrieving params');
        
        if (componentId== 'evaluator') {
            callback('ok', {
                            id:0,
                            skillDifWeight:0.3,
                            prefLength:60,
                            
                            drawishWeight:-0.1,
                            luckWeight:-0.1,
                            durationWeight:0.2,
                            resilienceWeight:0.1,
                            completionWeight:0.08,
                            favorsPositionWeight:-0.3,
                            uncertaintyLateWeight:0.2,
                            leadChangeWeight:-0.28,
                            permananceWeight:0.1,
                            killMovesWeight:0.36,
                            
                            clusteringKCoef:0.4,
                            strengthEvalDrawWeight:0.5,
                            stepDifWeight:1.0
                          });
        } else {//generator
            callback('ok', {
                            id:0,
                            
                            //Fine tune
                            shortFineTuneLimit:3,
                            shortFineTuneThresh:0.0,
                            fineTuneFamineLimit:7,
                            
                            //Instrinsic eval
                            idealNumPlayers:2,
                            numPlayersDrop:0.4,
                            invalidWeight:-100.0,
                            numPlayersWeight:1.0,
                            complexityWeight:-0.3
                          });
        }
    };
    
    Database.prototype.getAllUnscoredGames = function (callback) {
        console.log('Database is stubbed. Would be retrieving unscroed games');
        /*this.gameCollection.find({/*does not have score},{id:1, name:1, intrinsicScore:1, testLength:1, numPlayers:1, gdlVersion:1}, function(err, item) {
            if (item != null){
                var ret = [];
                for (var meta of item) {
                    ret.push({meta:meta, startedEval:false});
                }
                callback(err,ret);
	        } else
                callback(err,null);
        });*/
        //expects games: {meta:the meta data, startedEval:boolean, matches:[{id,playerOrder,rep,players}] stored in database}
        callback(null,[]);
    };
    
    Database.prototype.getTopGames = function (num,callback) {
        console.log('Database is stubbed. Would be retrieving top games');
        if (num<=0)
            num=1;
        var ret = [];
        var cursor = this.gameCollection.find({isGame:1},{id:1, name:1, gdlVersion:1, hlgdl:1, gdl:1, score:1},{'sort': [['score.evalScore','desc']], 'limit':num});//
        cursor.each(function(err, doc) {
            if (err) {
                callback(err,ret);
            } else if (doc != null) {
                ret.push(doc)
            } else {
                callback(err,ret);
            }
        });
        
        //expects games: {meta:the meta data, startedEval:boolean, matches:[{id,playerOrder,rep,players}] stored in database}
        //callback(null,[]);
    };
    
    Database.prototype.savePlayerTypeSkills = function (skillMap,callback) {
        var self=this;
	    skillMap.skillMap=1;
	    self.gameCollection.insert(skillMap, {w:1}, function(err, result) {
		    callback(err);
	    });
    }

    Database.prototype.getPlayerTypeSkill = function (type,callback) {
        this.gameCollection.findOne({skillMap:1}, function(err, item) {
            if (item != null)
                callback(err,item[type]);
	        else
                callback(err,null);
        });
    } 
    return Database
};

module.exports =  function() {
    
   var fs = require('fs');
    function Database(address,gdlDir) {
        //TODO...
        // Retrieve
        var self=this;
        if (gdlDir===undefined)
            self.gdlDir='./gdl';
        else
            self.gdlDir=gdlDir;
        fs.access(self.gdlDir,fs.F_OK, function(err) {
            if (err) {
                fs.mkdir(self.gdlDir, function(){});
            }
        });
        
        self.writing=[];
        
        self.mongo = require('mongodb').MongoClient;

        // Connect to the db (localhost:27017/exampleDb)
        self.mongo.connect("mongodb://"+address, function(err, db) {
          if(!err) {
            console.log("We are connected to the database.");
            db.collection('GAMES', function(err, collection) {
                if(!err) {
                    self.gameCollection=collection;
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
        //Be sure we don't overlap
        self.gameCollection.deleteMany({id:meta.id}, {w:1}, function(err, result) {
            if (err) {
                callback(err);
            } else {
                
                if (JSON.parse(result).n>0) {
                    var filePath = self.gdlDir+'/'+meta.id+'.gdl';
                    fs.access(filePath,fs.R_OK, function(err) {
                        if(!err) {
                            console.log('gdl file already exists. Deleting it.');
                            fs.unlink(filePath,function(err){
                                if (err)
                                    console.log('Could not remove prev existing gdl file '+filePath+': '+err);
                            });
                        }
                    });
                }
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
        });
        //console.log('Database is stubbed. Would be storing game '+meta.id+': '+meta.name);
        
    };
    
    /*Database.prototype.gdl = function (gameId,callback) {
        console.log('Database is stubbed. Would be retrieving gdl for '+gameId);
        callback('(role none)');
    }*/
    
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
    }
    
    Database.prototype.storeScore = function (gameId,score,callback) {
        //console.log('Database is stubbed. Soring score for '+gameId);
        this.gameCollection.update({id:gameId}, {$set:{score:score}},{w:1}, function(err, result) {
            if (err)
                callback(err);
            else
                callback('ok');
        });
    }
    
    Database.prototype.storeGameResults = function (results,callback) {
        //console.log('Database is stubbed. Soring game results for game: '+results.gameId);
        this.gameCollection.update({id:results.gameId}, {$push:{match_simulation_results:results}},{w:1}, function(err, result) {
            if (err)
                callback(err);
            else
                callback('ok');
        });
    }
    
    Database.prototype.getMaxMatchId = function (callback) {
        console.log('Database is stubbed. Would be retrieving max match id');
        //if it doesnt exist, create it
        callback(0);
    }
    
    Database.prototype.getLastParams = function (componentId,callback) {
        console.log('Database is stubbed. Would be retrieving params');
        
        if (componentId== 'evaluator') {
            callback('ok', {
                            id:0,
                            skillDifWeight:0.3,
                            prefLength:60,
                            drawishWeight:-0.2,
                            luckWeight:-0.2,
                            durationWeight:0.4,
                            resilienceWeight:0.4,
                            completionWeight:0.4
                          });
        } else {
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
    }
    
    Database.prototype.getAllUnscoredGames = function (callback) {
        console.log('Database is stubbed. Would be retrieving unscroed games');
        
        //expects games: {meta:the meta data, startedEval:boolean, matches:[{id,playerOrder,rep,players}] stored in database}
        callback([]);
    }
    
    
    return Database
};

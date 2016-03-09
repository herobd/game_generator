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
    
    Database.prototype.storeGame = function (meta,gdl,hlgdl)  {
        
    
        self.gameCollection.insert(meta, {w:1}, function(err, result) {
            self.gameCollection.update({id:meta.id}, {$set:{gdl:gdl, hlgdl:hlgdl}},{w:1}, function(err, result) {});
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
        fs.access(path,fs.R_OK, function(err) {
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
    
    Database.prototype.storeScore = function (gameId,score) {
        console.log('Database is stubbed. Soring score for '+gameId);
        
    }
    
    Database.prototype.storeGameResults = function (results,callback) {
        console.log('Database is stubbed. Soring game results for game: '+results.gameId);
        callback('ok');
    }
    
    Database.prototype.getMaxMatchId = function (callback) {
        console.log('Database is stubbed. Would be retrieving max match id');
        //if it doesnt exist, create it
        callback(0);
    }
    
    Database.prototype.getLastParams = function (componentId,callback) {
        console.log('Database is stubbed. Would be retrieving params');
        
        
        callback('ok', {
                    id:0,
                    skillDifWeight:0.3,
                    prefLength:60
                  });
    }
    
    
    return Database
};

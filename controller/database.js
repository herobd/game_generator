module.exports =  function() {
    
       
    function Database() {
        //TODO...
        // Retrieve
        /*self.mongo = require('mongodb').MongoClient;

        // Connect to the db
        self.mongo.connect("mongodb://localhost:27017/exampleDb", function(err, db) {
          if(!err) {
            console.log("We are connected");
          }
        });*/
    }
    
    Database.prototype.storeGame = function (meta,gdl,hlgdl)  {
        //TODO...
        console.log('Database is stubbed. Would be storing game '+meta.id+': '+meta.name);
        //and write gdl to file
    };
    
    Database.prototype.gdl = function (gameId) {
        console.log('Database is stubbed. Would be retrieving gdl for '+gameId);
        return '(role none)';
    }
    
    Database.prototype.gdlFileLocation = function (gameId) {
        console.log('Database is stubbed. Would be retrieving gdl file location for '+gameId);
        return './nowhere.none';
    }
    
    
    return Database
};

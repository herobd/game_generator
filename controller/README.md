Controller server for our General Rudimental Evolutionary Game Generator (GREGG)

## Requirements:
- Node.js  (incldues packages)
  - express
  - request
  - fs
  - body-parser
  - multer
  - mongodb

Just `npm install [ ]` them

Accepts

GET
* `/connect?id=<component name[generator,evaluator]>&address=<address of component>` informs controller the address of the component server
* `/disconnect?id=<component name[generator,evaluator]>` informs controller the component server will no longer be reachable

POST
* `/submit_game` with:    (this is open to modification, depending on what's easier for you to send over)
  * meta:json with elemnts: id,score,testLength[short,med,long],gdlVersion[1,2],numPlayers,name(optional)
    * the id is expected to be qunique for each submission. If submitting different versions of the same game (eg fine tuning), be sure the id's are different (perhaps use a version number after it, eg "ticTacToe1.2"). 
  * gdl: text of gdl file
  * hlgdl: text of high-level gdl file (I have these to seperate as 

What will this store?
* Games that have given to it (GDL,[high],ref to gene params,meta)
* Logs of simulations of games (raw/parsed?)
* Evaluations of games (score object, ref to eval params)
* Parameters of the evaluation function (whenever they change)
* Parameters of the genetic algorithm




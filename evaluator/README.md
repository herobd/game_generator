Evaluation server for our General Rudimental Evolutionary Game Generator (GREGG)

## Requirements:
- Node.js  (incldues packages)
  - express
  - request
  - fs
  - body-parser
  - multer

Just `npm install [ ]` them

Accepts

GET
* `/connect?id=<component name[controller]>&address=<address of component>` informs evaluator the address of the component server
* `/disconnect?id=<component name[controller]>` informs evaluator the component server will no longer be reachable

POST
* 




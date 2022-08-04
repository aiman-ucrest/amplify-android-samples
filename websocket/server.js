// server.js

const WebSocket = require('ws')

const wss = new WebSocket.Server({ port: 8080 })

// ref: https://www.npmjs.com/package/ws#api-docs
// ref: https://github.com/websockets/ws/blob/master/doc/ws.md#event-close-1

function heartbeat() {
  this.isAlive = true;
}

wss.on('connection', function connection(ws, req) {
  ws.isAlive = true
  //ws.on('pong', heartbeat)

  ws.on('message', message => {
    console.log(`Received message => ${message}`)
  console.log(`Reply 'Pong!'`)
    ws.send('Pong!')
  })

  ws.on('close', function clear(code, reason) {
  console.log(`Client closed:: code= ${code}, reason= ${reason}`)
})

  // on every new connection
  console.log(`New connection established! IP: ${req.socket.remoteAddress}`)
  console.log(`Saying hi to the client..`)
  ws.send('First hello from server! :)')
})

const interval = setInterval(function ping() {
  wss.clients.forEach(function each(ws) {
    if (ws.isAlive === false) return ws.terminate();

    ws.isAlive = false;
    ws.ping();
  });
}, 30000)

wss.on('close', function close() {
  console.log(`Server closed::`)
  clearInterval(interval);
})
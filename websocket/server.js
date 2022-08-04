// server.js

const WebSocket = require('ws')

const wss = new WebSocket.Server({ port: 8080 })

wss.on('connection', function connection(ws, req) {
  ws.on('message', message => {
    console.log(`Received message => ${message}`)
  console.log(`Reply 'Pong!'`)
    ws.send('Pong!')
  })
  console.log(`New connection established! IP: ${req.socket.remoteAddress}`)
  console.log(`Saying hi to the client..`)
  ws.send('First hello from server! :)')
})
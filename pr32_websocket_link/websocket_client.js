// websocket_client.js

const WebSocket = require('ws');
const readline = require('readline');

const socket = new WebSocket('ws://localhost:8080');

let position = { x: 0, y: 0 };

socket.on('open', () => {
  console.log('Connected to server');
  listenForKeys();
});

socket.on('message', data => {
  const message = JSON.parse(data);
  if (message.type === 'end') {
    console.log(`Partida finalitzada. Distància recorreguda: ${message.distance}`);
  }
});

function listenForKeys() {
  readline.emitKeypressEvents(process.stdin);
  process.stdin.setRawMode(true);

  console.log('Usa les fletxes per moure el jugador. Prem "q" per sortir.');

  process.stdin.on('keypress', (str, key) => {
    if (key.sequence === '\u0003') {
      closeClient();
    }
  
    if (key.name === 'q') {
      closeClient();
    }
  
    switch (key.name) {
      case 'up': position.y += 1; break;
      case 'down': position.y -= 1; break;
      case 'left': position.x -= 1; break;
      case 'right': position.x += 1; break;
      default: return; // ignora altres tecles
    }
  
    socket.send(JSON.stringify(position));
    console.log(`Moviment: (${position.x}, ${position.y})`);
  });
}

function closeClient() {
  console.log('\nTancant client WebSocket...');
  socket.close();
  process.stdin.setRawMode(false);
  process.stdin.pause();
  process.exit();
}

process.on('SIGINT', () => {
  closeClient();
});

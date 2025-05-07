// websocket_server.js

const WebSocket = require('ws');
const { MongoClient } = require('mongodb');
const winston = require('winston');

// Logger
const logger = winston.createLogger({
  level: 'info',
  transports: [
    new winston.transports.Console(),
    new winston.transports.File({ filename: 'logs/server.log' })
  ]
});

// MongoDB
const mongoUrl = 'mongodb://localhost:27017';
const dbName = 'gameDB';
const collectionName = 'movements';

let db, collection;

MongoClient.connect(mongoUrl)
  .then(client => {
    db = client.db(dbName);
    collection = db.collection(collectionName);
    logger.info('Connected to MongoDB');
  })
  .catch(err => {
    logger.error('MongoDB connection error: ' + err);
  });

// WebSocket server
const wss = new WebSocket.Server({ port: 8080 });
logger.info('WebSocket server running on ws://localhost:8080');

// Sessions map: socket -> session
const sessions = new Map(); // socket -> sessionData

wss.on('connection', socket => {
  logger.info('Client connected');

  let lastActivity = Date.now();
  sessions.set(socket, null); // cap sessió activa al principi

  const interval = setInterval(() => {
    const session = sessions.get(socket);
    if (session && Date.now() - lastActivity > 10000) {
      // Inactivitat de 10s → finalitza la partida
      const { startX, startY, endX, endY, sessionId } = session;
      const dist = Math.sqrt((endX - startX) ** 2 + (endY - startY) ** 2);

      socket.send(JSON.stringify({ type: 'end', distance: dist.toFixed(2) }));
      logger.info(`Sessió ${sessionId} finalitzada. Distància: ${dist.toFixed(2)}`);

      // Es podria guardar la distància a MongoDB aquí, si vols

      sessions.set(socket, null); // Espera nova partida
    }
  }, 1000);

  socket.on('message', async message => {
    lastActivity = Date.now();

    try {
      const data = JSON.parse(message);
      const { x, y } = data;

      let session = sessions.get(socket);

      if (!session) {
        // Nova partida
        const sessionId = Date.now() + '_' + Math.floor(Math.random() * 1000);
        session = { sessionId, startX: x, startY: y, endX: x, endY: y };
        logger.info(`Nova partida iniciada: ${sessionId}`);
      } else {
        // Partida en curs
        session.endX = x;
        session.endY = y;
      }

      sessions.set(socket, session);

      await collection.insertOne({
        sessionId: session.sessionId,
        timestamp: new Date(),
        position: { x, y }
      });

      logger.info(`Moviment sessió ${session.sessionId}: (${x}, ${y})`);
    } catch (e) {
      logger.error('Error processant el missatge: ' + e.message);
    }
  });

  socket.on('close', () => {
    logger.info('Client disconnected');
    sessions.delete(socket);
    clearInterval(interval);
  });
});

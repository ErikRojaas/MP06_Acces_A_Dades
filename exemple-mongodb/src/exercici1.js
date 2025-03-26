const fs = require('fs');
const path = require('path');
const { MongoClient } = require('mongodb');
const xml2js = require('xml2js');
const he = require('he');  
const winston = require('winston');  

const xmlFilePath = path.join(__dirname, '../../data/Posts.xml');

// Guardar logs
const logger = winston.createLogger({
  level: 'info',
  transports: [
    new winston.transports.File({ filename: path.join(__dirname, 'data', 'logs', 'exercici1.log') }),
    new winston.transports.Console()
  ]
});

// Leer y analizar el archivo XML
async function parseXMLFile(filePath) {
  try {
    const xmlData = fs.readFileSync(filePath, 'utf-8');
    const parser = new xml2js.Parser({
      explicitArray: false,
      mergeAttrs: true
    });

    return new Promise((resolve, reject) => {
      parser.parseString(xmlData, (err, result) => {
        if (err) {
          reject(err);
        } else {
          resolve(result);
        }
      });
    });
  } catch (error) {
    logger.error('Error leyendo o analizando el archivo XML:', error);
    throw error;
  }
}

function decodeHtmlEntities(str) {
  return he.decode(str);
}

// Extraer 10000 preguntas con más ViewCount
function processQuestionsData(posts) {
  // Ordenar descendentemente las preguntas por ViewCount
  const questions = posts.filter(post => post.PostTypeId === '1'); 
  questions.sort((a, b) => parseInt(b.ViewCount) - parseInt(a.ViewCount));

  // Seleccionar solo las 10.0000 primeras
  const topQuestions = questions.slice(0, 10000);

  // Procesar los datos para MongoDB
  return topQuestions.map(post => ({
    question: {
      Id: post.Id,
      PostTypeId: post.PostTypeId,
      AcceptedAnswerId: post.AcceptedAnswerId || null,
      CreationDate: post.CreationDate,
      Score: post.Score,
      ViewCount: post.ViewCount,
      Body: decodeHtmlEntities(post.Body), 
      OwnerUserId: post.OwnerUserId,
      LastActivityDate: post.LastActivityDate,
      Title: post.Title,
      Tags: decodeHtmlEntities(post.Tags), 
      AnswerCount: post.AnswerCount,
      CommentCount: post.CommentCount,
      ContentLicense: post.ContentLicense
    }
  }));
}

// Función principal para cargar los datos a MongoDB
async function loadDataToMongoDB() {
  // Configuración de la conexión a MongoDB
  const uri = process.env.MONGODB_URI || 'mongodb://root:password@localhost:27017/';
  const client = new MongoClient(uri);
  
  try {
    await client.connect();
    logger.info('Conectado a MongoDB');
    
    const database = client.db('stackexchange');
    const collection = database.collection('posts');
    
    // Leer y analizar el archivo XML
    logger.info('Leyendo el archivo XML...');
    const xmlData = await parseXMLFile(xmlFilePath);
    
    // Procesar las preguntas
    logger.info('Procesando las preguntas...');
    const posts = processQuestionsData(xmlData.posts.row);
    
    // Eliminar datos existentes (opcional)
    logger.info('Eliminando datos existentes...');
    await collection.deleteMany({});
    
    // Insertar las nuevas preguntas
    logger.info('Insertando preguntas a MongoDB...');
    const result = await collection.insertMany(posts);
    
    logger.info(`${result.insertedCount} documentos insertados correctamente.`);
    logger.info('Datos cargados con éxito!');
    
  } catch (error) {
    logger.error('Error cargando los datos a MongoDB:', error);
  } finally {
    await client.close();
    logger.info('Conexión a MongoDB cerrada');
  }
}

// Ejecutar la función principal
loadDataToMongoDB();
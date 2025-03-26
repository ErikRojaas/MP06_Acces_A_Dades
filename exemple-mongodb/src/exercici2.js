const { MongoClient } = require('mongodb');
const fs = require('fs');
const path = require('path');
const pdf = require('pdfkit');  // Usamos 'pdfkit' para generar el PDF
const winston = require('winston');

// Configuración del logger
const logger = winston.createLogger({
  level: 'info',
  transports: [
    new winston.transports.File({ filename: path.join(__dirname, 'data', 'logs', 'exercici2.log') }),
    new winston.transports.Console()
  ]
});

// Configuración de la conexión a MongoDB
const uri = process.env.MONGODB_URI || 'mongodb://root:password@localhost:27017/';
const client = new MongoClient(uri);

// Letras clave para la segunda consulta
const searchKeywords = ["pug", "wig", "yak", "nap", "jig", "mug", "zap", "gag", "oaf", "elf"];

// Función para obtener la media de ViewCount
async function getAverageViewCount(collection) {
  const result = await collection.aggregate([
    { $match: { "question.ViewCount": { $gt: 0 } } },  // Filtramos solo las preguntas con ViewCount > 0
    { $addFields: { "question.ViewCount": { $toInt: "$question.ViewCount" } } }, // Convertir ViewCount a número
    { $group: { _id: null, avgViewCount: { $avg: "$question.ViewCount" } } }
  ]).toArray();

  return result[0] ? result[0].avgViewCount : 0;
}

// Función para generar un archivo PDF con los títulos de las preguntas
function generatePdf(filePath, title, questions) {
  const doc = new pdf();
  doc.pipe(fs.createWriteStream(filePath));

  doc.fontSize(18).text(title, { align: 'center' });
  doc.moveDown();
  doc.fontSize(12);

  questions.forEach((question, index) => {
    doc.text(`${index + 1}. ${question}`);
  });

  doc.end();
}

// Función para obtener las preguntas con ViewCount mayor que la media
async function getQuestionsAboveAverage(collection) {
  const avgViewCount = await collection.aggregate([
    { 
      $group: { 
        _id: null, 
        averageViewCount: { $avg: { $toDouble: "$question.ViewCount" } }  // Convertir ViewCount a número
      } 
    }
  ]).toArray();

  if (avgViewCount.length === 0) {
    console.log("No se pudo calcular la media de ViewCount.");
    return [];
  }

  const averageViewCount = avgViewCount[0].averageViewCount;
  console.log(`ViewCount promedio: ${averageViewCount}`);

  const questions = await collection.find({
    'question.ViewCount': { $gt: averageViewCount }  // Comparar con ViewCount convertido a número
  }).limit(100).toArray();
  
  console.log(`Preguntas encontradas: ${questions.length}`);
  return questions;
}

// Función para obtener las preguntas con títulos coincidentes
async function getQuestionsByTitles(collection) {
  const regex = new RegExp(searchKeywords.join("|"), "i");  // Unir palabras clave con OR en la expresión regular

  const questions = await collection.find({
    "question.Title": { $regex: regex }
  }).limit(100).toArray();

  console.log(`Preguntas con títulos coincidentes: ${questions.length}`);
  return questions;
}

// Función principal para ejecutar las consultas y generar los informes
async function executeQueries() {
  try {
    await client.connect();
    logger.info('Conectado a MongoDB');
    
    const database = client.db('stackexchange');
    const collection = database.collection('posts');
    
    // 1. Obtener la media de ViewCount
    const avgViewCount = await getAverageViewCount(collection);
    logger.info(`La media de ViewCount es: ${avgViewCount}`);
    
    // 2. Consultar preguntas con ViewCount mayor que la media
    const questionsAboveAvg = await getQuestionsAboveAverage(collection);
    logger.info(`Número de preguntas con ViewCount mayor que la media: ${questionsAboveAvg.length}`);

    // 3. Consultar preguntas con ciertas letras en el título
    const questionsWithKeywords = await getQuestionsByTitles(collection);
    logger.info(`Número de preguntas con palabras clave en el título: ${questionsWithKeywords.length}`);

    // 4. Generar los informes en PDF
    const questionsAboveAvgTitles = questionsAboveAvg.map(q => q.question.Title);  // Asegúrate de acceder correctamente a 'Title'
    const questionsWithKeywordsTitles = questionsWithKeywords.map(q => q.question.Title);  // Asegúrate de acceder correctamente a 'Title'

    // Si no hay preguntas, evitar generar un PDF vacío
    if (questionsAboveAvgTitles.length > 0) {
      generatePdf(path.join(__dirname, 'data', 'out', 'informe1.pdf'), 'Preguntas con ViewCount mayor que la media', questionsAboveAvgTitles);
    } else {
      logger.info('No se encontraron preguntas con ViewCount mayor que la media.');
    }

    if (questionsWithKeywordsTitles.length > 0) {
      generatePdf(path.join(__dirname, 'data', 'out', 'informe2.pdf'), 'Preguntas con palabras clave en el título', questionsWithKeywordsTitles);
    } else {
      logger.info('No se encontraron preguntas con palabras clave en el título.');
    }

    logger.info('Informes generados con éxito!');
  } catch (error) {
    logger.error('Error en la ejecución de las consultas:', error);
  } finally {
    await client.close();
    logger.info('Conexión a MongoDB cerrada');
  }
}

// Ejecutar la función principal
executeQueries();

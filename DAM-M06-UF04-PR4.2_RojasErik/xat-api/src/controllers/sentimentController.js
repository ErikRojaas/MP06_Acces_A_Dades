const axios = require('axios');
const { logger } = require('../config/logger');

const OLLAMA_API_URL = process.env.CHAT_API_OLLAMA_URL || 'http://localhost:11434/api';
const DEFAULT_OLLAMA_MODEL = process.env.CHAT_API_OLLAMA_MODEL || 'llama3:latest';

/**
 * Analitza el sentiment d'un text donat utilitzant Ollama
 * @route POST /api/chat/sentiment
 */
const analyzeSentiment = async (req, res, next) => {
    try {
        const { text } = req.body;

        if (!text || typeof text !== 'string') {
            logger.warn('Text invàlid per a l\'anàlisi de sentiment');
            return res.status(400).json({ message: 'El text és obligatori i ha de ser una cadena' });
        }

        logger.info('Analitzant sentiment del text', { textLength: text.length });

        // Crear el prompt detallado para Ollama
        const prompt = `Responde solo con un objeto JSON con las siguientes claves, en sentiment pon si la frase es positiva, negativa o neutra, y en score pon si es negativa, numeros negativos (del 0 al -1, dependiendo de la nagatividad) y si es positiva en numeros positivos (del 0 al 1, dependiendo de la positividad), sin explicaciones ni comentarios adicionales. Si no puedes identificar un dato, usa "Desconegut" o "Desconeguda" según corresponda:
        {
            "text": "${text}",
            "sentiment": "Desconegut",
            "score": 0.0,
            "timestamp": "Desconegut"
        }`;

        // Llamada a Ollama para obtener la respuesta
        const ollamaResponse = await generateResponse(prompt);

        // Intentamos parsear la respuesta como JSON
        let responseJson;
        try {
            responseJson = JSON.parse(ollamaResponse);
        } catch (error) {
            logger.error('Error al parsear la respuesta de Ollama', { response: ollamaResponse });
            return res.status(500).json({ message: 'Error al procesar la respuesta' });
        }

        // Verificamos que la respuesta tenga los campos correctos
        if (!responseJson.text || responseJson.sentiment === "Desconegut" || !responseJson.score || responseJson.timestamp === "Desconegut") {
            logger.warn('Respuesta de Ollama incompleta o errónea', { responseJson });
            return res.status(400).json({ 
                message: 'La respuesta no tiene el formato esperado o contiene datos desconocidos',
                response: responseJson
            });
        }

        logger.info('Anàlisi de sentiment completada', {
            sentiment: responseJson.sentiment,
            score: responseJson.score
        });

        res.json(responseJson);
    } catch (error) {
        logger.error('Error en l\'anàlisi de sentiment', { error: error.message });
        next(error);
    }
};

/**
 * Funció per generar una resposta amb Ollama
 * @param {string} prompt - Text d'entrada per generar la resposta
 * @param {Object} options - Opcions de configuració
 * @returns {Promise<string>} Resposta generada
 */
const generateResponse = async (prompt, options = {}) => {
    try {
        const {
            model = DEFAULT_OLLAMA_MODEL,
            stream = false
        } = options;

        logger.debug('Iniciant generació de resposta', { 
            model, 
            stream,
            promptLength: prompt.length 
        });

        const requestBody = {
            model,
            prompt,
            stream
        };

        const response = await axios.post(`${OLLAMA_API_URL}/generate`, requestBody, {
            timeout: 30000,
            responseType: stream ? 'stream' : 'json'
        });

        return response.data.response.trim();
    } catch (error) {
        logger.error('Error en la generació de resposta', {
            error: error.message,
            model: options.model,
            stream: options.stream
        });
        
        return 'Ho sento, no he pogut generar una resposta en aquest moment.';
    }
};

module.exports = { analyzeSentiment };

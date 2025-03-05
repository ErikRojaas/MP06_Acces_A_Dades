// src/exercici3.js
const fs = require('fs').promises;
const path = require('path');
require('dotenv').config();
const { logger } = require('../../xat-api/src/config/logger'); // Ajusta la ruta si cal

const IMAGES_SUBFOLDER = 'imatges/animals';
const IMAGE_TYPES = ['.jpg', '.jpeg', '.png', '.gif'];
const OLLAMA_URL = process.env.CHAT_API_OLLAMA_URL;
const OLLAMA_MODEL = process.env.CHAT_API_OLLAMA_MODEL_VISION;
const OUTPUT_FILE_PATH = path.resolve(__dirname, '..', '..', 'data', 'exercici3_resposta.json');

// Funció per llegir un fitxer i convertir-lo a Base64
async function imageToBase64(imagePath) {
    try {
        const data = await fs.readFile(imagePath);
        return Buffer.from(data).toString('base64');
    } catch (error) {
        console.error(`Error al llegir o convertir la imatge ${imagePath}:`, error.message);
        return null;
    }
}

// Funció per gestionar els errors d'Ollama de forma centralitzada
function handleOllamaError(error) {
    let errorMessage = 'Error en la petició a Ollama';
    let details = {
        url: `${OLLAMA_URL}/generate`,
        model: OLLAMA_MODEL,
    };

    if (error.response) {
        errorMessage += `: ${error.response.status} ${error.response.statusText}`;
        details.response = error.response.data;
    } else if (error.request) {
        errorMessage += ': No s\'ha rebut resposta';
    }
    logger.error(errorMessage, { ...details, error: error.message }); // Usa el logger
    return null;
}


// Funció per fer la petició a Ollama
async function queryOllama(base64Image, prompt) {
    const requestBody = {
        model: OLLAMA_MODEL,
        prompt: prompt,
        images: [base64Image],
        stream: false
    };

    try {
        logger.info('Enviant petició a Ollama...');

        let response = await fetch(`${OLLAMA_URL}/generate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            throw new Error(`Error HTTP`); // Llança error genèric
        }

        const data = await response.json();

        if (!data || !data.response) {
            throw new Error('La resposta d\'Ollama no té el format esperat');
        }

        return data.response;
    } catch (error) {
         return handleOllamaError(error);  // Utilitza la funció centralitzada
    }
}
// Funció per escriure la resposta en un fitxer JSON
async function saveResponseToFile(responses) {
    const responseStructure = {
        analisis: responses
    };

    try {
        // Escrivim la resposta al fitxer
        await fs.writeFile(OUTPUT_FILE_PATH, JSON.stringify(responseStructure, null, 2));
        console.log(`Resposta guardada a ${OUTPUT_FILE_PATH}`);
    } catch (error) {
        console.error('Error al guardar la resposta:', error.message);
    }
}

// Funció principal
async function main() {
    try {
        if (!process.env.DATA_PATH) {
            throw new Error('La variable d\'entorn DATA_PATH no està definida.');
        }
        if (!OLLAMA_URL) {
            throw new Error('La variable d\'entorn CHAT_API_OLLAMA_URL no està definida.');
        }
        if (!OLLAMA_MODEL) {
            throw new Error('La variable d\'entorn CHAT_API_OLLAMA_MODEL no està definida.');
        }

        const imagesFolderPath = path.resolve(__dirname, '..', '..', 'data', IMAGES_SUBFOLDER);
        try {
            await fs.access(imagesFolderPath);
        } catch (error) {
            throw new Error(`El directori d'imatges no existeix: ${imagesFolderPath}`);
        }

        const animalDirectories = await fs.readdir(imagesFolderPath);
        let allResponses = []; // Guardar totes les respostes

        for (const animalDir of animalDirectories) {
            const animalDirPath = path.join(imagesFolderPath, animalDir);

            try {
                const stats = await fs.stat(animalDirPath);
                if (!stats.isDirectory()) {
                    console.log(`S'ignora l'element no directori: ${animalDirPath}`);
                    continue;
                }
            } catch (error) {
                console.error(`Error al obtenir informació del directori: ${animalDirPath}`, error.message);
                continue;
            }

            const imageFiles = await fs.readdir(animalDirPath);

            for (const imageFile of imageFiles) {
                const imagePath = path.join(animalDirPath, imageFile);
                const ext = path.extname(imagePath).toLowerCase();
                
                if (!IMAGE_TYPES.includes(ext)) {
                    console.log(`S'ignora fitxer no vàlid: ${imagePath}`);
                    continue;
                }

                const base64String = await imageToBase64(imagePath);

                if (base64String) {
                    console.log(`\nProcessant imatge: ${imagePath}`);
                    console.log(`Mida de la imatge en Base64: ${base64String.length} caràcters`);
                    
                    // Actualitzem el prompt per demanar més detalls sobre l'animal
                    const prompt = `
                        Responde solo con un objeto JSON con las siguientes claves, sin explicaciones ni comentarios adicionales. Si no puedes identificar un dato, usa "Desconegut" o "Desconeguda" según corresponda:
                        {
                            "nom_comu": "valor",
                            "nom_cientific": "valor",
                            "taxonomia": {
                                "classe": "valor",
                                "ordre": "valor",
                                "familia": "valor"
                            },
                            "habitat": {
                                "tipus": ["valor"],
                                "regioGeografica": ["valor"],
                                "clima": ["valor"]
                            },
                            "dieta": {
                                "tipus": "valor",
                                "aliments_principals": ["valor"]
                            },
                            "caracteristiques_fisiques": {
                                "mida": {
                                    "altura_mitjana_cm": "valor",
                                    "pes_mitja_kg": "valor"
                                },
                                "colors_predominants": ["valor"],
                                "trets_distintius": ["valor"]
                            },
                            "estat_conservacio": {
                                "classificacio_IUCN": "valor",
                                "amenaces_principals": ["valor"]
                            }
                        }
                    `;
                    
                    
                    let response = await queryOllama(base64String, prompt);

                    if (response) {
                        console.log(`\nResposta d'Ollama per ${imageFile}:`);
                        console.log(response);
                        response = JSON.parse(response);

                        // Crear l'estructura de la resposta per aquest fitxer d'imatge
                        const analysis = {
                            imatge: {
                                nom_fitxer: imageFile,
                            },
                            analisi: {
                                nom_comu: response.nom_comu || "Desconegut",  // Nom comú de l'animal
                                nom_cientific: response.nom_cientific || "Desconegut",  // Nom científic
                                taxonomia: {
                                    classe: response.taxonomia?.classe || "Desconeguda",  // Classe taxonòmica, amb validació per evitar l'error
                                    ordre: response.taxonomia?.ordre || "Desconegut",  // Ordre taxonòmico, amb validació
                                    familia: response.taxonomia?.familia || "Desconeguda",  // Família taxonòmica, amb validació
                                },
                                habitat: {
                                    tipus: response.habitat?.tipus || ["Desconegut"],  // Tipus d'hàbitat, amb validació
                                    regioGeografica: response.habitat?.regioGeografica || ["Desconeguda"],  // Regió geogràfica
                                    clima: response.habitat?.clima || ["Desconegut"],  // Tipus de clima
                                },
                                dieta: {
                                    tipus: response.dieta?.tipus || "Desconegut",  // Tipus de dieta
                                    aliments_principals: response.dieta?.aliments_principals || ["Desconeguts"],  // Llista d'aliments
                                },
                                caracteristiques_fisiques: {
                                    mida: {
                                        altura_mitjana_cm: response.caracteristiques_fisiques?.mida?.altura_mitjana_cm || "Desconeguda",  // Alçada mitjana
                                        pes_mitja_kg: response.caracteristiques_fisiques?.mida?.pes_mitja_kg || "Desconegut",  // Pes mitjà
                                    },
                                    colors_predominants: response.caracteristiques_fisiques?.colors_predominants || ["Desconeguts"],  // Colors predominants
                                    trets_distintius: response.caracteristiques_fisiques?.trets_distintius || ["Desconeguts"],  // Trets distintius
                                },
                                estat_conservacio: {
                                    classificacio_IUCN: response.estat_conservacio?.classificacio_IUCN || "Desconeguda",  // Classificació IUCN
                                    amenaces_principals: response.estat_conservacio?.amenaces_principals || ["Desconegudes"],  // Amenaces principals
                                }
                            }
                        };

                        // Afegim l'anàlisi d'aquesta imatge
                        allResponses.push(analysis);
                    } else {
                        console.error(`\nNo s'ha rebut resposta vàlida per ${imageFile}`);
                    }
                    console.log('------------------------');
                }
            }
            console.log(`\nNaturem l'execució després d'iterar el contingut del primer directori`);
            break; // Aturem l'execució després d'iterar el contingut del primer directori
        }

        // Després de processar totes les imatges, guardem les respostes al fitxer
        await saveResponseToFile(allResponses);

    } catch (error) {
        console.error('Error durant l\'execució:', error.message);
    }
}

// Executem la funció principal
main();

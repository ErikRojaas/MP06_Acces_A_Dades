package com.project.pr14;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.objectes.Llibre;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Classe principal que gestiona la lectura i el processament de fitxers JSON per obtenir dades de llibres.
 */
public class PR14GestioLlibreriaJacksonMain {

    private final File dataFile;

    /**
     * Constructor de la classe PR14GestioLlibreriaJacksonMain.
     *
     * @param dataFile Fitxer on es troben els llibres.
     */
    public PR14GestioLlibreriaJacksonMain(File dataFile) {
        this.dataFile = dataFile;
    }

    public static void main(String[] args) {
        File dataFile = new File(System.getProperty("user.dir"), "data/pr14" + File.separator + "llibres_input.json");
        PR14GestioLlibreriaJacksonMain app = new PR14GestioLlibreriaJacksonMain(dataFile);
        app.processarFitxer();
    }

    /**
     * Processa el fitxer JSON per carregar, modificar, afegir, esborrar i guardar les dades dels llibres.
     */
    public void processarFitxer() {
        List<Llibre> llibres = carregarLlibres();
        if (llibres != null) {
            modificarAnyPublicacio(llibres, 1, 1995);
            afegirNouLlibre(llibres, new Llibre(4, "Històries de la ciutat", "Miquel Soler", 2022));
            esborrarLlibre(llibres, 2);
            guardarLlibres(llibres);
        }
    }

    /**
     * Carrega els llibres des del fitxer JSON.
     *
     * @return Llista de llibres o null si hi ha hagut un error en la lectura.
     */
    public List<Llibre> carregarLlibres() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Carrega la llista de llibres des del fitxer JSON
            return objectMapper.readValue(dataFile, new TypeReference<List<Llibre>>() {});
        } catch (IOException e) {
            System.err.println("Error llegint el fitxer JSON: " + e.getMessage());
            return null;
        }
    }

    /**
     * Modifica l'any de publicació d'un llibre amb un id específic.
     *
     * @param llibres Llista de llibres.
     * @param id Identificador del llibre a modificar.
     * @param nouAny Nou any de publicació.
     */
    public void modificarAnyPublicacio(List<Llibre> llibres, int id, int nouAny) {
        for (Llibre llibre : llibres) {
            if (llibre.getId() == id) {
                llibre.setAny(nouAny);
                break;
            }
        }
    }

    /**
     * Afegeix un nou llibre a la llista de llibres.
     *
     * @param llibres Llista de llibres.
     * @param nouLlibre Nou llibre a afegir.
     */
    public void afegirNouLlibre(List<Llibre> llibres, Llibre nouLlibre) {
        llibres.add(nouLlibre);
    }

    /**
     * Esborra un llibre amb un id específic de la llista de llibres.
     *
     * @param llibres Llista de llibres.
     * @param id Identificador del llibre a esborrar.
     */
    public void esborrarLlibre(List<Llibre> llibres, int id) {
        Iterator<Llibre> iterator = llibres.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId() == id) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Guarda la llista de llibres en un fitxer nou.
     *
     * @param llibres Llista de llibres a guardar.
     */
    public void guardarLlibres(List<Llibre> llibres) {
        ObjectMapper objectMapper = new ObjectMapper();
        // Crear la ruta del fitxer de sortida en la carpeta "data/pr14"
        File outputDir = new File(System.getProperty("user.dir"), "data/pr14");
        File outputFile = new File(outputDir, "llibres_output_jackson.json");
    
        try {
            // Crear el directori si no existeix
            if (!outputDir.exists()) {
                outputDir.mkdirs(); // Crear la carpeta si no existeix
            }
    
            // Escriu la llista de llibres al fitxer JSON sense pretty print (en una sola línia)
            objectMapper.writeValue(outputFile, llibres);
            
            System.out.println("Llibres guardats correctament a: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error escrivint el fitxer JSON: " + e.getMessage());
        }
    }
    
}

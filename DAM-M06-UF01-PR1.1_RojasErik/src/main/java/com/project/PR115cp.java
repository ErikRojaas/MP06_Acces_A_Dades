package com.project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PR115cp {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Error: Has d'indicar dues rutes d'arxiu.");
            System.out.println("Ús: PR115cp <origen> <destinació>");
            return;
        }

        // Ruta de l'arxiu origen
        String rutaOrigen = args[0];
        // Ruta de l'arxiu destinació
        String rutaDesti = args[1];

        // Crida al mètode per copiar l'arxiu
        copiarArxiu(rutaOrigen, rutaDesti);
    }

    // Mètode per copiar un arxiu de text de l'origen al destí
    public static void copiarArxiu(String rutaOrigen, String rutaDesti) {
        Path pathOrigen = Paths.get(rutaOrigen);
        Path pathDesti = Paths.get(rutaDesti);

        // Comprovar si el fitxer d'origen existeix i és un fitxer
        if (!Files.exists(pathOrigen) || !Files.isRegularFile(pathOrigen)) {
            System.out.println("Error: El fitxer d'origen no existeix o no és un fitxer.");
            return;
        }

        // Comprovar si l'arxiu de destinació ja existeix
        if (Files.exists(pathDesti)) {
            System.out.println("Advertència: L'arxiu de destinació ja existeix i serà sobreescrit.");
        }

        // Intentar copiar el contingut de l'arxiu origen al destinació
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathOrigen.toFile()), StandardCharsets.UTF_8));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pathDesti.toFile()), StandardCharsets.UTF_8))
        ) {
            String linia;
            boolean ultimaLiniaEnBlanc = false;

            // Copiar línia per línia
            while ((linia = reader.readLine()) != null) {
                writer.println(linia);
                if (linia.isEmpty()) {
                    ultimaLiniaEnBlanc = true; // Recordar si la última línea es en blanco
                }
            }

            // Si l'última línia del fitxer origen era en blanc, afegir una línia en blanc al destinació
            if (ultimaLiniaEnBlanc) {
                writer.println(); // Afegeix una línia en blanc
            }

            System.out.println("Còpia realitzada correctament.");
        } catch (IOException e) {
            System.out.println("Error: Hi ha hagut un problema en copiar el fitxer.");
            e.printStackTrace();
        }
    }
}

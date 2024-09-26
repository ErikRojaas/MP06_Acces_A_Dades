package com.project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class PR113append {

    public static void main(String[] args) {
        // Definir el camí del fitxer dins del directori "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/frasesMatrix.txt";

        // Crida al mètode que afegeix les frases al fitxer
        afegirFrases(camiFitxer);
    }

    // Mètode que afegeix les frases al fitxer amb UTF-8 i línia en blanc final
    public static void afegirFrases(String camiFitxer) {
        String[] frases = {
            "I can only show you the door",
            "You're the one that has to walk through it",
            ""
        };

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(camiFitxer, true))) {
            for (String frase : frases) {
                writer.write(frase);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al afegir al fitxer: " + e.getMessage());
        }
    }
}

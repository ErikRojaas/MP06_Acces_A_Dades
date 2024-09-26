package com.project;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PR110ReadFile {

    public static void main(String[] args) {
        String camiFitxer = System.getProperty("user.dir") + "/data/GestioTasques.java";
        llegirIMostrarFitxer(camiFitxer);  // Només cridem a la funció amb la ruta del fitxer
    }

    // Funció que llegeix el fitxer i mostra les línies amb numeració
    public static void llegirIMostrarFitxer(String camiFitxer) {
        // Intentar llegir el fitxer i mostrar el seu contingut
        try (BufferedReader reader = new BufferedReader(new FileReader(camiFitxer))) {
            String line;
            int lineNumber = 1;
            
            // Llegir cada línia i mostrar-la amb el número de línia
            while ((line = reader.readLine()) != null) {
                System.out.println(lineNumber + ": " + line);
                lineNumber++;
            }
        } catch (IOException e) {
            // Gestionar els errors de lectura i mostrar un missatge adequat
            System.err.println("Error llegint el fitxer: " + e.getMessage());
        }
    }
}

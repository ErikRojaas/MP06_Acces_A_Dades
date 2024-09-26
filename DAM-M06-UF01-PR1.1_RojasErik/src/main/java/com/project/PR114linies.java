package com.project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class PR114linies {

    public static void main(String[] args) {
        // Definir el camí del fitxer dins del directori "data"
        String camiFitxer = System.getProperty("user.dir") + "/data/numeros.txt";

        // Crida al mètode que genera i escriu els números aleatoris
        generarNumerosAleatoris(camiFitxer);
    }

    // Mètode per generar 10 números aleatoris i escriure'ls al fitxer
    public static void generarNumerosAleatoris(String camiFitxer) {
        Random random = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(camiFitxer, StandardCharsets.UTF_8))) {
            for (int i = 0; i < 10; i++) {
                int numero = random.nextInt(100); // Generar número aleatori entre 0 i 99
                writer.write(String.valueOf(numero)); // Escriure el número com a cadena
                if (i < 9) {
                    writer.newLine(); // Afegir un salt de línia per a tots excepte l'últim
                }
            }
        } catch (IOException e) {
            System.out.println("Error al escriure al fitxer: " + e.getMessage());
        }
    }
}

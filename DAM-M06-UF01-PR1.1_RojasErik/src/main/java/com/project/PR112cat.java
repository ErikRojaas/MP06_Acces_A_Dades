package com.project;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PR112cat {

    public static void main(String[] args) {
        // Comprovar si s'ha passat un argument
        if (args.length != 1) {
            System.out.println("No s'ha especificat cap ruta d'arxiu.");
            return;
        }

        // Obtenir la ruta passada com a argument
        String ruta = args[0];
        mostrarContingutArxiu(ruta);
    }

    public static void mostrarContingutArxiu(String ruta) {
        Path path = Paths.get(ruta);
        File fitxer = path.toFile();

        // Comprovar si és un fitxer existent
        if (!fitxer.exists()) {
            System.out.println("El fitxer no existeix o no és accessible.");
            return;
        }

        // Comprovar si és una carpeta
        if (fitxer.isDirectory()) {
            System.out.println("El path no correspon a un arxiu, sinó a una carpeta.");
            return;
        }

        // Intentar llegir el fitxer i mostrar el seu contingut
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fitxer), StandardCharsets.UTF_8))) {
            String linia;
            while ((linia = reader.readLine()) != null) {
                System.out.println(linia);
            }
        } catch (IOException e) {
            System.out.println("El fitxer no existeix o no és accessible.");
        }
    }
}

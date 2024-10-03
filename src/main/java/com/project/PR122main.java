package com.project;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.project.excepcions.IOFitxerExcepcio;
import com.project.objectes.PR122persona;

public class PR122main {
    private static String filePath = System.getProperty("user.dir") + "/data/PR122persones.dat";

    public static void main(String[] args) {
        List<PR122persona> persones = new ArrayList<>();
        persones.add(new PR122persona("Maria", "López", 36));
        persones.add(new PR122persona("Gustavo", "Ponts", 63));
        persones.add(new PR122persona("Irene", "Sales", 54));

        try {
            serialitzarPersones(persones);
            List<PR122persona> deserialitzades = deserialitzarPersones();
            deserialitzades.forEach(System.out::println);  // Mostra la informació per pantalla
        } catch (IOFitxerExcepcio e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Mètode per serialitzar la llista de persones
    public static void serialitzarPersones(List<PR122persona> persones) throws IOFitxerExcepcio {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(persones);
            System.out.println("Persones serialitzades correctament.");
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en serialitzar la llista de persones", e);
        }
    }

    // Mètode per deserialitzar la llista de persones
    public static List<PR122persona> deserialitzarPersones() throws IOFitxerExcepcio {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<PR122persona>) ois.readObject();
        } catch (FileNotFoundException e) {
            throw new IOFitxerExcepcio("Fitxer no trobat", e);
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en deserialitzar la llista de persones", e);
        } catch (ClassNotFoundException e) {
            throw new IOFitxerExcepcio("Classe no trobada durant la deserialització", e);
        }
    }

    // Getter i Setter per a filePath (opcional)
    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }
}

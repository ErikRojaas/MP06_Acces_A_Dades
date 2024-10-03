package com.project;

import com.project.objectes.PR121hashmap;
import com.project.excepcions.IOFitxerExcepcio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class PR121mainEscriu {
    private static String filePath = System.getProperty("user.dir") + "/data/PR121HashMapData.ser";

    public static void main(String[] args) {
        PR121hashmap hashMap = new PR121hashmap();
        hashMap.getPersones().put("Carla", 22);
        hashMap.getPersones().put("Bernat", 30);
        hashMap.getPersones().put("Anna", 25);

        try {
            serialitzarHashMap(hashMap);
        } catch (IOFitxerExcepcio e) {
            System.err.println(e.getMessage());
        }
    }

    public static void serialitzarHashMap(PR121hashmap hashMap) throws IOFitxerExcepcio { // Afegit throws
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(hashMap);
            System.out.println("HashMap serialitzat correctament.");
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en serialitzar l'objecte HashMap", e);
        }
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        PR121mainEscriu.filePath = filePath;
    }
}

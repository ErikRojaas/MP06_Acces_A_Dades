package com.project;

import com.project.objectes.PR121hashmap;
import com.project.excepcions.IOFitxerExcepcio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class PR121mainLlegeix {
    private static String filePath = System.getProperty("user.dir") + "/data/PR121HashMapData.ser";

    public static void main(String[] args) {
        try {
            PR121hashmap hashMap = deserialitzarHashMap();
            hashMap.getPersones().forEach((nom, edat) -> System.out.println(nom + ": " + edat + " anys"));
        } catch (IOFitxerExcepcio e) {
            System.err.println(e.getMessage());
        }
    }

    public static PR121hashmap deserialitzarHashMap() throws IOFitxerExcepcio { // Afegit throws
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (PR121hashmap) ois.readObject();
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en deserialitzar l'objecte HashMap", e);
        } catch (ClassNotFoundException e) {
            throw new IOFitxerExcepcio("Classe no trobada durant la deserialització", e);
        }
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String filePath) {
        PR121mainLlegeix.filePath = filePath;
    }
}

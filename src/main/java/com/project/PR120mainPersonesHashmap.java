package com.project;

import com.project.excepcions.IOFitxerExcepcio;

import java.io.*;
import java.util.HashMap;

public class PR120mainPersonesHashmap {

    // Ruta del fitxer per defecte
    private static String filePath = System.getProperty("user.dir") + "/data/PR120persones.dat";

    // Mètode per escriure les persones a un arxiu
    public static void escriurePersones(HashMap<String, Integer> persones) throws IOFitxerExcepcio {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            for (String nom : persones.keySet()) {
                dos.writeUTF(nom);
                dos.writeInt(persones.get(nom));
            }
            System.out.println("Les dades s'han escrit correctament a: " + filePath);
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en escriure les persones al fitxer", e);
        }
    }

    // Mètode per llegir les persones des d'un arxiu
    public static void llegirPersones() throws IOFitxerExcepcio {
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            System.out.println("Contingut del fitxer " + filePath + ":");
            while (dis.available() > 0) {
                String nom = dis.readUTF();
                int edat = dis.readInt();
                System.out.println(nom + ": " + edat + " anys");
            }
        } catch (FileNotFoundException e) {
            throw new IOFitxerExcepcio("Error en llegir les persones del fitxer: Fitxer no trobat", e);
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error en llegir les persones del fitxer", e);
        }
    }

    // Mètode main per executar el programa
    public static void main(String[] args) {
        HashMap<String, Integer> persones = new HashMap<>();
        persones.put("Carla", 22);
        persones.put("Bernat", 30);
        persones.put("David", 35);
        persones.put("Anna", 25);
        persones.put("Elena", 28);

        try {
            // Escriure les persones al fitxer
            escriurePersones(persones);

            // Llegir i mostrar les persones des del fitxer
            llegirPersones();
        } catch (IOFitxerExcepcio e) {
            System.err.println(e.getMessage());
        }
    }

    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }
}

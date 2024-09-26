package com.project;

import java.io.File;
import java.io.IOException;

public class PR111Files {

    public static void main(String[] args) {
        // Aquí aniria el teu codi original, o bé podries cridar el mètode gestionarArxius amb una ruta fixa
        String folderPath = System.getProperty("user.dir") + "/data/pr111/myFiles";
        gestionarArxius(folderPath);
    }

    public static void gestionarArxius(String folderPath) {
        // Creem la carpeta myFiles dins del directori donat
        File folder = new File(folderPath);

        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (folderCreated) {
                System.out.println("Carpeta creada: " + folderPath);
            } else {
                System.out.println("No s'ha pogut crear la carpeta.");
                return;
            }
        }

        // Creem els dos arxius file1.txt i file2.txt dins de la carpeta
        File file1 = new File(folderPath + "/file1.txt");
        File file2 = new File(folderPath + "/file2.txt");

        try {
            if (file1.createNewFile()) {
                System.out.println("Arxiu creat: " + file1.getName());
            }
            if (file2.createNewFile()) {
                System.out.println("Arxiu creat: " + file2.getName());
            }
        } catch (IOException e) {
            System.out.println("Error en crear els arxius.");
            e.printStackTrace();
        }

        // Renombrar l'arxiu file2.txt a renamedFile.txt
        File renamedFile = new File(folderPath + "/renamedFile.txt");
        if (file2.renameTo(renamedFile)) {
            System.out.println("Arxiu renombrat a: " + renamedFile.getName());
        } else {
            System.out.println("No s'ha pogut renombrar l'arxiu.");
        }

        // Mostrar un llistat dels arxius dins de la carpeta myFiles
        System.out.println("Els arxius de la carpeta són:");
        showFilesInDirectory(folder);

        // Eliminar l'arxiu file1.txt
        if (file1.delete()) {
            System.out.println("L'arxiu " + file1.getName() + " ha estat eliminat.");
        } else {
            System.out.println("No s'ha pogut eliminar l'arxiu " + file1.getName());
        }

        // Tornar a mostrar un llistat dels arxius dins de la carpeta myFiles
        System.out.println("Els arxius de la carpeta són:");
        showFilesInDirectory(folder);
    }

    // Funció per mostrar tots els arxius dins d'un directori
    private static void showFilesInDirectory(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        } else {
            System.out.println("No s'ha trobat la carpeta o no hi ha arxius.");
        }
    }
}

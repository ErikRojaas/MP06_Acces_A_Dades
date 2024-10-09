package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * Classe principal que gestiona la lectura i el processament de fitxers XML per obtenir dades de persones.
 */
public class PR130Main {

    private final File dataDir;

    public PR130Main(File dataDir) {
        this.dataDir = dataDir;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File dataDir = new File(userDir, "data" + File.separator + "pr13");

        PR130Main app = new PR130Main(dataDir);
        app.processarFitxerXML("persones.xml");
    }

    public void processarFitxerXML(String filename) {
        File inputFile = new File(dataDir, filename);
        Document doc = parseXML(inputFile);
        if (doc != null) {
            NodeList persones = doc.getElementsByTagName("persona");

            // Imprimeix les capçaleres
            System.out.println(getCapçaleres());

            // Itera sobre les persones i imprimeix les dades
            for (int i = 0; i < persones.getLength(); i++) {
                Element persona = (Element) persones.item(i);
                String nom = persona.getElementsByTagName("nom").item(0).getTextContent();
                String cognom = persona.getElementsByTagName("cognom").item(0).getTextContent();
                String edat = persona.getElementsByTagName("edat").item(0).getTextContent();
                String ciutat = persona.getElementsByTagName("ciutat").item(0).getTextContent();

                // Imprimeix les dades de la persona amb el format adequat
                System.out.println(formatarPersona(nom, cognom, edat, ciutat));
            }
        }
    }

    /**
     * Llegeix un fitxer XML i el converteix en un objecte Document.
     */
    public static Document parseXML(File inputFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retorna les capçaleres alineades.
     */
    public static String getCapçaleres() {
        return String.format("%-8s %-14s %-5s %-9s", "Nom", "Cognom", "Edat", "Ciutat") +
               "\n-------- -------------- ----- ---------";
    }

    /**
     * Formata una persona perquè es mostri alineada.
     */
    public static String formatarPersona(String nom, String cognom, String edat, String ciutat) {
        return String.format("%-8s %-14s %-5s %-9s", nom, cognom, edat, ciutat);
    }
}

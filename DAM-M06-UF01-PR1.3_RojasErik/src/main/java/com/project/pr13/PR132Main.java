package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.project.pr13.format.AsciiTablePrinter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe principal que permet gestionar un fitxer XML de cursos amb opcions per llistar, afegir i eliminar alumnes, 
 * així com mostrar informació dels cursos i mòduls.
 * 
 * Aquesta classe inclou funcionalitats per interactuar amb un fitxer XML, executar operacions de consulta,
 * i realitzar modificacions en el contingut del fitxer.
 */
public class PR132Main {

    private final Path xmlFilePath;
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Constructor de la classe PR132Main.
     * 
     * @param xmlFilePath Ruta al fitxer XML que conté la informació dels cursos.
     */
    public PR132Main(Path xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    /**
     * Mètode principal que inicia l'execució del programa.
     * 
     * @param args Arguments passats a la línia de comandament (no s'utilitzen en aquest programa).
     */
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        Path xmlFilePath = Paths.get(userDir, "data", "pr13", "cursos.xml");

        PR132Main app = new PR132Main(xmlFilePath);
        app.executar();
    }

    /**
     * Executa el menú principal del programa fins que l'usuari decideixi sortir.
     */
    public void executar() {
        boolean exit = false;
        while (!exit) {
            mostrarMenu();
            System.out.print("Escull una opció: ");
            int opcio = scanner.nextInt();
            scanner.nextLine(); // Netegem el buffer del scanner
            exit = processarOpcio(opcio);
        }
    }

    /**
     * Processa l'opció seleccionada per l'usuari.
     * 
     * @param opcio Opció seleccionada al menú.
     * @return True si l'usuari decideix sortir del programa, false en cas contrari.
     */
    public boolean processarOpcio(int opcio) {
        String cursId;
        String nomAlumne;
        
        if (opcio == 1) {
            List<List<String>> cursos = llistarCursos();
            imprimirTaulaCursos(cursos);
        } else if (opcio == 2) {
            System.out.print("Introdueix l'ID del curs per veure els seus mòduls: ");
            cursId = scanner.nextLine();
            List<List<String>> moduls = mostrarModuls(cursId);
            imprimirTaulaModuls(moduls);
        } else if (opcio == 3) {
            System.out.print("Introdueix l'ID del curs per veure la llista d'alumnes: ");
            cursId = scanner.nextLine();
            List<String> alumnes = llistarAlumnes(cursId);
            imprimirLlistaAlumnes(alumnes);
        } else if (opcio == 4) {
            System.out.print("Introdueix l'ID del curs on vols afegir l'alumne: ");
            cursId = scanner.nextLine();
            System.out.print("Introdueix el nom complet de l'alumne a afegir: ");
            nomAlumne = scanner.nextLine();
            afegirAlumne(cursId, nomAlumne);
        } else if (opcio == 5) {
            System.out.print("Introdueix l'ID del curs on vols eliminar l'alumne: ");
            cursId = scanner.nextLine();
            System.out.print("Introdueix el nom complet de l'alumne a eliminar: ");
            nomAlumne = scanner.nextLine();
            eliminarAlumne(cursId, nomAlumne);
        } else if (opcio == 6) {
            System.out.println("Sortint del programa...");
            return true;
        } else {
            System.out.println("Opció no reconeguda. Si us plau, prova de nou.");
        }
        return false;
    }

    /**
     * Mostra el menú principal amb les opcions disponibles.
     */
    private void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Llistar IDs de cursos i tutors");
        System.out.println("2. Mostrar IDs i títols dels mòduls d'un curs");
        System.out.println("3. Llistar alumnes d’un curs");
        System.out.println("4. Afegir un alumne a un curs");
        System.out.println("5. Eliminar un alumne d'un curs");
        System.out.println("6. Sortir");
    }

    /**
     * Llegeix el fitxer XML i llista tots els cursos amb el seu tutor i nombre d'alumnes.
     * 
     * @return Llista amb la informació dels cursos (ID, tutor, nombre d'alumnes).
     */
    public List<List<String>> llistarCursos() {
        List<List<String>> cursos = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/cursos/curs";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element curs = (Element) nodeList.item(i);
                String id = curs.getAttribute("id");
                String tutor = xPath.compile("tutor").evaluate(curs);
                NodeList alumnesNodeList = (NodeList) xPath.compile("alumnes/alumne").evaluate(curs, XPathConstants.NODESET);
                int totalAlumnes = alumnesNodeList.getLength();

                List<String> cursInfo = List.of(id, tutor, String.valueOf(totalAlumnes));
                cursos.add(cursInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursos;
    }

    /**
     * Imprimeix per consola una taula amb la informació dels cursos.
     * 
     * @param cursos Llista amb la informació dels cursos.
     */
    public void imprimirTaulaCursos(List<List<String>> cursos) {
        List<String> capçaleres = List.of("ID", "Tutor", "Total Alumnes");
        AsciiTablePrinter.imprimirTaula(capçaleres, cursos);
    }

    /**
     * Mostra els mòduls d'un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs del qual es volen veure els mòduls.
     * @return Llista amb la informació dels mòduls (ID, títol).
     */
    public List<List<String>> mostrarModuls(String idCurs) {
        List<List<String>> moduls = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/cursos/curs[@id='" + idCurs + "']/moduls/modul";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element modul = (Element) nodeList.item(i);
                String id = modul.getAttribute("id");
                String titol = xPath.compile("titol").evaluate(modul);

                List<String> modulInfo = List.of(id, titol);
                moduls.add(modulInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moduls;
    }

    /**
     * Imprimeix per consola una taula amb la informació dels mòduls.
     * 
     * @param moduls Llista amb la informació dels mòduls.
     */
    public void imprimirTaulaModuls(List<List<String>> moduls) {
        List<String> capçaleres = List.of("ID Mòdul", "Títol");
        AsciiTablePrinter.imprimirTaula(capçaleres, moduls);
    }

    /**
     * Llista els alumnes inscrits en un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs del qual es volen veure els alumnes.
     * @return Llista amb els noms dels alumnes.
     */
    public List<String> llistarAlumnes(String idCurs) {
        List<String> alumnes = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/cursos/curs[@id='" + idCurs + "']/alumnes/alumne";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                alumnes.add(nodeList.item(i).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return alumnes;
    }

    /**
     * Imprimeix per consola la llista d'alumnes.
     * 
     * @param alumnes Llista amb els noms dels alumnes.
     */
    public void imprimirLlistaAlumnes(List<String> alumnes) {
        if (alumnes.isEmpty()) {
            System.out.println("No hi ha alumnes en aquest curs.");
        } else {
            alumnes.forEach(alumne -> System.out.println("- " + alumne));
        }
    }

    /**
     * Afegeix un alumne nou a un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs on s'ha d'afegir l'alumne.
     * @param nomAlumne Nom complet de l'alumne a afegir.
     */
    public void afegirAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/cursos/curs[@id='" + idCurs + "']/alumnes";
            Node alumnesNode = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);

            if (alumnesNode != null) {
                Element nouAlumne = document.createElement("alumne");
                nouAlumne.setTextContent(nomAlumne);
                alumnesNode.appendChild(nouAlumne);
                guardarDocumentXML(document);
                System.out.println("Alumne afegit correctament.");
            } else {
                System.out.println("No s'ha trobat el curs especificat.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un alumne d'un curs especificat pel seu ID.
     * 
     * @param idCurs ID del curs del qual es vol eliminar l'alumne.
     * @param nomAlumne Nom complet de l'alumne a eliminar.
     */
    public void eliminarAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/cursos/curs[@id='" + idCurs + "']/alumnes/alumne[text()='" + nomAlumne + "']";
            Node alumneNode = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);

            if (alumneNode != null) {
                alumneNode.getParentNode().removeChild(alumneNode);
                guardarDocumentXML(document);
                System.out.println("Alumne eliminat correctament.");
            } else {
                System.out.println("No s'ha trobat l'alumne especificat.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega un fitxer XML des del disc i el converteix en un document DOM.
     * 
     * @param xmlFilePath Ruta al fitxer XML.
     * @return Document DOM carregat des del fitxer XML.
     */
    public Document carregarDocumentXML(Path xmlFilePath) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlFilePath.toFile());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error carregant el document XML.");
        }
    }

    /**
     * Desa un document DOM modificat en el fitxer XML original.
     * 
     * @param document Document DOM modificat.
     */
    public void guardarDocumentXML(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFilePath.toFile());
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}

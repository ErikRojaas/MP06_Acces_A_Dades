package com.project;

import java.io.File;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Collection;

import com.project.dao.Manager;
import com.project.domain.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Iniciant el programa de gestió de biblioteca...");

        // 1. Configuració inicial
        System.out.println("\n=== Configuració inicial ===");
        String basePath = System.getProperty("user.dir") + "/data/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            if(!dir.mkdirs()) {
                System.out.println("Error en la creació de la carpeta 'data'");
                return;
            }
        }
        System.out.println("Directori de dades verificat: " + basePath);

        // 2. Inicialitzem Hibernate
        Manager.createSessionFactory();
        System.out.println("Hibernate inicialitzat correctament");

        // 3. Creem els autors
        System.out.println("\n=== Creant autors ===");
        Autor autorGarcia = Manager.addAutor("Gabriel García Márquez");
        Autor autorOrwell = Manager.addAutor("George Orwell");
        Autor autorWoolf = Manager.addAutor("Virginia Woolf");

        
        // Comprovem que s'han creat els autors
        @SuppressWarnings("unchecked")
        Collection<Autor> autors = (Collection<Autor>) Manager.listCollection(Autor.class);
        System.out.println("Autors creats:");
        System.out.println(Manager.collectionToString(Autor.class, autors));

        // 4. Creem els llibres
        System.out.println("\n=== Creant llibres ===");
        Llibre llibre100 = Manager.addLlibre(
            "978-0307474728", "Cien años de soledad",
            "Vintage Español", 1967);
        Llibre llibre1984 = Manager.addLlibre(
            "978-0451524935", "1984",
            "Signet Classic", 1949);
        Llibre llibreDalloway = Manager.addLlibre(
            "978-0156628709", "Mrs. Dalloway",
            "Harvest Books", 1925);

        // Comprovem que s'han creat els llibres
        @SuppressWarnings("unchecked")
        Collection<Llibre> llibres = (Collection<Llibre>) Manager.listCollection(Llibre.class);
        System.out.println("Llibres creats:");
        System.out.println(Manager.collectionToString(Llibre.class, llibres));

        // 5. Assignem autors als llibres
        System.out.println("\n=== Vinculant autors amb llibres ===");
        Set<Llibre> llibresGarcia = new HashSet<>();
        llibresGarcia.add(llibre100);
        Manager.updateAutor(autorGarcia.getAutorId(), autorGarcia.getNom(), llibresGarcia);

        Set<Llibre> llibresOrwell = new HashSet<>();
        llibresOrwell.add(llibre1984);
        Manager.updateAutor(autorOrwell.getAutorId(), autorOrwell.getNom(), llibresOrwell);

        Set<Llibre> llibresWoolf = new HashSet<>();
        llibresWoolf.add(llibreDalloway);
        Manager.updateAutor(autorWoolf.getAutorId(), autorWoolf.getNom(), llibresWoolf);

        // Comprovem les relacions autor-llibre
        System.out.println("Relacions autor-llibre establertes:");
        System.out.println(Manager.collectionToString(Autor.class, 
            (Collection<Autor>) Manager.listCollection(Autor.class)));

        // 6. Creem les biblioteques
        System.out.println("\n=== Creant biblioteques ===");
        Biblioteca bibCentre = Manager.addBiblioteca(
            "Biblioteca Central", "Barcelona", 
            "Carrer Central 123", "93123456", "central@biblio.cat");
        Biblioteca bibNord = Manager.addBiblioteca(
            "Biblioteca Nord", "Barcelona", 
            "Avinguda Nord 45", "93789012", "nord@biblio.cat");

        // Comprovem que s'han creat les biblioteques
        @SuppressWarnings("unchecked")
        Collection<Biblioteca> biblioteques = (Collection<Biblioteca>) Manager.listCollection(Biblioteca.class);
        System.out.println("Biblioteques creades:");
        System.out.println(Manager.collectionToString(Biblioteca.class, biblioteques));

        // 7. Creem exemplars
        System.out.println("\n=== Creant exemplars ===");
        Exemplar ex100_1 = Manager.addExemplar("100-001", llibre100, bibCentre);
        Exemplar ex100_2 = Manager.addExemplar("100-002", llibre100, bibNord);
        Exemplar ex1984_1 = Manager.addExemplar("1984-001", llibre1984, bibCentre);
        Exemplar ex1984_2 = Manager.addExemplar("1984-002", llibre1984, bibNord);
        Exemplar exDall_1 = Manager.addExemplar("DALL-001", llibreDalloway, bibCentre);

        // Comprovem que s'han creat els exemplars
        @SuppressWarnings("unchecked")
        Collection<Exemplar> exemplars = (Collection<Exemplar>) Manager.listCollection(Exemplar.class);
        System.out.println("Exemplars creats:");
        System.out.println(Manager.collectionToString(Exemplar.class, exemplars));

        // 8. Creem persones
        System.out.println("\n=== Creant usuaris ===");
        Persona persona1 = Manager.addPersona(
            "11111111A", "Joan Garcia",
            "666111222", "joan@email.com");
        Persona persona2 = Manager.addPersona(
            "22222222B", "Maria Puig",
            "666333444", "maria@email.com");
        Persona persona3 = Manager.addPersona(
            "33333333C", "Pere Soler",
            "666555666", "pere@email.com");

        // Comprovem que s'han creat les persones
        @SuppressWarnings("unchecked")
        Collection<Persona> persones = (Collection<Persona>) Manager.listCollection(Persona.class);
        System.out.println("Usuaris creats:");
        System.out.println(Manager.collectionToString(Persona.class, persones));

        // 9. Creem préstecs
        System.out.println("\n=== Creant préstecs ===");
        LocalDate avui = LocalDate.now();
        
        System.out.println("Creant préstec actiu per a persona1...");
        Prestec prestec1 = Manager.addPrestec(ex100_1, persona1, 
            avui, avui.plusDays(15));
        
        System.out.println("Creant préstec actiu per a persona2...");
        Prestec prestec2 = Manager.addPrestec(ex1984_1, persona2, 
            avui.minusDays(5), avui.plusDays(10));
        
        System.out.println("Creant i retornant préstec per a persona3...");
        Prestec prestec3 = Manager.addPrestec(exDall_1, persona3, 
            avui.minusDays(20), avui.minusDays(5));
        Manager.registrarRetornPrestec(prestec3.getPrestecId(), avui.minusDays(7));

        // Comprovem l'estat dels préstecs
        @SuppressWarnings("unchecked")
        Collection<Prestec> prestecs = (Collection<Prestec>) Manager.listCollection(Prestec.class);
        System.out.println("Estat actual dels préstecs:");
        System.out.println(Manager.collectionToString(Prestec.class, prestecs));

        // 10. Realitzem les consultes demanades
        System.out.println("\n=== Execució de les consultes específiques ===");
        
        System.out.println("\nConsulta 1: Llibres amb els seus autors");
        List<Llibre> llibresAmbAutors = Manager.findLlibresAmbAutors();
        System.out.println(Manager.collectionToString(Llibre.class, llibresAmbAutors));

        System.out.println("\nConsulta 2: Llibres en préstec i qui els té");
        List<Object[]> prestecsActius = Manager.findLlibresEnPrestec();
        System.out.println(Manager.formatMultipleResult(prestecsActius));

        System.out.println("\nConsulta 3: Llibres i les seves biblioteques");
        List<Object[]> llibresBiblios = Manager.findLlibresAmbBiblioteques();
        System.out.println(Manager.formatMultipleResult(llibresBiblios));
        

        // 11. Tanquem la connexió
        System.out.println("\n=== Finalitzant el programa ===");
        Manager.close();
        System.out.println("Connexió tancada. Programa finalitzat.");
    }
}
package com.project;

import org.junit.jupiter.api.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CiutatCiutadaTest {
    
    private static Ciutat testCiutat;
    private static Ciutada testCiutada1;
    private static Ciutada testCiutada2;
    
    @BeforeAll
    public static void setup() {
        // Inicialitzar Hibernate
        Manager.createSessionFactory();
    }
    
    @AfterAll
    public static void cleanup() {
        // Tancar la sessió de Hibernate
        Manager.close();
    }
    
    @Test
    @Order(1)
    public void testCreateCart() {
        // Provar la creació d'un nou carret
        testCiutat = Manager.addCiutat("Ciutat de Prova", "Pais", 8970);
        assertNotNull(testCiutat, "La ciutat no hauria de ser null després de crear-lo");
        assertTrue(testCiutat.getCiutatId() > 0, "La ciutat hauria de tenir un ID vàlid després de crear-lo");
        assertEquals("Ciutat de Prova", testCiutat.getNom(), "El nom de ciutat hauria de coincidir amb l'entrada");
        assertEquals("Pais", testCiutat.getPais(), "El pais de ciutat hauria de coincidir amb l'entrada");
        assertEquals(8970, testCiutat.getPoblacio(), "La poblacio de ciutat hauria de coincidir amb l'entrada");
        assertTrue(testCiutat.getCiutadans().isEmpty(), "La nova ciutat hauria de tenir el conjunt de ciutadans buit");
    }
    
    @Test
    @Order(2)
    public void testCreateItems() {
        // Provar la creació de nous items
        testCiutada1 = Manager.addCiutada("Ciutada de Prova 1", "Cognom1", 23);
        testCiutada2 = Manager.addCiutada("Ciutada de Prova 2", "Cognom2", 18);
        
        assertNotNull(testCiutada1, "El ciutada 1 no hauria de ser null després de crear-lo");
        assertNotNull(testCiutada2, "El ciutada 2 no hauria de ser null després de crear-lo");
        assertTrue(testCiutada1.getCiutadaId() > 0, "El ciutada 1 hauria de tenir un ID vàlid");
        assertTrue(testCiutada2.getCiutadaId() > 0, "El ciutada 2 hauria de tenir un ID vàlid");
    }
    
    @Test
    @Order(3)
    public void testAddItemsToCart() {
        // Crear un conjunt d'items
        Set<Ciutada> ciutadans = new HashSet<>();
        ciutadans.add(testCiutada1);
        ciutadans.add(testCiutada2);
        
        // Actualitzar el carret amb els nous items
        Manager.updateCiutat(testCiutat.getCiutatId(), testCiutat.getNom(), testCiutat.getPais(), testCiutat.getPoblacio(), ciutadans);
        
        // Obtenir el carret actualitzat de la base de dades
        Ciutat updatedCiutat = Manager.getCiutatWithCiutadans(testCiutat.getCiutatId());
        
        assertNotNull(updatedCiutat, "La ciutat actualitzada no hauria de ser null");
        assertEquals(2, updatedCiutat.getCiutadans().size(), "La ciutat hauria de tenir 2 ciutadans");
        assertTrue(updatedCiutat.getCiutadans().contains(testCiutada1), "La ciutat hauria de contenir el ciutada 1");
        assertTrue(updatedCiutat.getCiutadans().contains(testCiutada2), "La ciutat hauria de contenir el ciutada 2");
    }
    
    @Test
    @Order(4)
    public void testUpdateItem() {
        // Actualitzar el nom de l'item
        String newName = "Item Actualitzat 1";
        Manager.updateCiutada(testCiutada1.getCiutadaId(), newName, "Cognom1 actualitzat", 33);
        
        // Obtenir l'item actualitzat
        Ciutada updatedItem = Manager.getById(Ciutada.class, testCiutada1.getCiutadaId());
        assertEquals(newName, updatedItem.getNom(), "El nom del ciutada hauria d'estar actualitzat");
        assertEquals("Cognom1 actualitzat", updatedItem.getCognom(), "El cognom del ciutada hauria d'estar actualitzat");
        assertEquals(33, updatedItem.getEdat(), "L'edat del ciutada hauria d'estar actualitzat");
    }
    
    @Test
    @Order(5)
    public void testListItems() {
        // Provar llistar tots els items
        Collection<?> ciutadans = Manager.listCollection(Ciutada.class);
        assertNotNull(ciutadans, "La col·lecció de ciutadans no hauria de ser null");
        assertTrue(ciutadans.size() >= 2, "Hauria d'haver-hi almenys 2 ciutadans");
    }
    
    @Test
    @Order(6)
    public void testRemoveItemFromCart() {
        // Obtenir carret amb items
        Ciutat ciutat = Manager.getCiutatWithCiutadans(testCiutat.getCiutatId());
        Set<Ciutada> ciutadans = new HashSet<>(ciutat.getCiutadans());
        
        // Eliminar un item
        ciutadans.remove(testCiutada1);
        Manager.updateCiutat(ciutat.getCiutatId(), ciutat.getNom(), ciutat.getPais(), ciutat.getPoblacio(), ciutadans);
        
        // Verificar l'actualització
        Ciutat updatedCiutat = Manager.getCiutatWithCiutadans(testCiutat.getCiutatId());
        assertEquals(1, updatedCiutat.getCiutadans().size(), "La ciutat hauria de tenir 1 ciutada després de l'eliminació");
        assertFalse(updatedCiutat.getCiutadans().contains(testCiutada1), "La ciutat no hauria de contenir el ciutada eliminat");
        assertTrue(updatedCiutat.getCiutadans().contains(testCiutada2), "La ciutat encara hauria de contenir el ciutada restant");
    }
    
    @Test
    @Order(7)
    public void testDeleteItems() {
        // Eliminar items
        Manager.delete(Ciutada.class, testCiutada1.getCiutadaId());
        Manager.delete(Ciutada.class, testCiutada2.getCiutadaId());
        
        // Verificar l'eliminació
        assertNull(Manager.getById(Ciutada.class, testCiutada1.getCiutadaId()), "El ciutada 1 hauria d'estar eliminat");
        assertNull(Manager.getById(Ciutada.class, testCiutada2.getCiutadaId()), "El ciutada 2 hauria d'estar eliminat");
    }
    
    @Test
    @Order(8)
    public void testDeleteCart() {
        // Eliminar carret
        Manager.delete(Ciutat.class, testCiutat.getCiutatId());
        
        // Verificar l'eliminació
        assertNull(Manager.getById(Ciutat.class, testCiutat.getCiutatId()), "La ciutat hauria d'estar eliminat");
    }
}
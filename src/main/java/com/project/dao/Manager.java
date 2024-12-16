package com.project.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import com.project.domain.*;
import java.util.ArrayList;
import java.util.HashSet;
import org.hibernate.query.NativeQuery;

public class Manager {
    private static SessionFactory factory;

    /**
     * Crea la SessionFactory per defecte
     */
    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            
            // Registrem totes les classes que tenen anotacions JPA
            configuration.addAnnotatedClass(Biblioteca.class);
            configuration.addAnnotatedClass(Llibre.class);
            configuration.addAnnotatedClass(Exemplar.class);
            configuration.addAnnotatedClass(Prestec.class);
            configuration.addAnnotatedClass(Persona.class);
            configuration.addAnnotatedClass(Autor.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("No s'ha pogut crear la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Crea la SessionFactory amb un fitxer de propietats específic
     */
    public static void createSessionFactory(String propertiesFileName) {
        try {
            Configuration configuration = new Configuration();
            
            configuration.addAnnotatedClass(Biblioteca.class);
            configuration.addAnnotatedClass(Llibre.class);
            configuration.addAnnotatedClass(Exemplar.class);
            configuration.addAnnotatedClass(Prestec.class);
            configuration.addAnnotatedClass(Persona.class);
            configuration.addAnnotatedClass(Autor.class);

            Properties properties = new Properties();
            try (InputStream input = Manager.class.getClassLoader().getResourceAsStream(propertiesFileName)) {
                if (input == null) {
                    throw new IOException("No s'ha trobat " + propertiesFileName);
                }
                properties.load(input);
            }

            configuration.addProperties(properties);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
                
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Error creant la SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Tanca la SessionFactory
     */
    public static void close() {
        if (factory != null) {
            factory.close();
        }
    }
    
    
    /**
     * Afegeix un nou autor.
     */
    public static Autor addAutor(String nom) {
        Autor result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                result = new Autor(nom);
                session.persist(result);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }

    /**
     * Actualitza un autor existent.
     */
    public static void updateAutor(long autorId, String nom, Set<Llibre> llibres) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Obtenemos el autor por su ID
                Autor autor = session.get(Autor.class, autorId);
                if (autor != null) {
                    // Actualizamos el nombre del autor
                    autor.setNom(nom);

                    // Actualizamos los libros asociados
                    for (Llibre llibre : llibres) {
                        if (!llibre.getAutors().contains(autor)) {
                            // Aseguramos que el autor esté asociado al libro
                            llibre.getAutors().add(autor);
                            session.saveOrUpdate(llibre);  // Guardamos los cambios en el libro
                        }
                    }

                    // Realizamos el merge del autor para guardar los cambios
                    session.merge(autor);
                }
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
    }
    
    
    /**
     * Afegeix un nou llibre.
     */
    public static Llibre addLlibre(String isbn, String titol, String editorial, int anyPublicacio) {
        Llibre result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                result = new Llibre(isbn, titol, editorial, anyPublicacio);
                session.persist(result);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
    /**
     * Afegeix una nova biblioteca.
     */
    public static Biblioteca addBiblioteca(String nom, String ciutat, String adreca, String telefon, String email) {
        Biblioteca result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                result = new Biblioteca(nom, ciutat, adreca, telefon, email);
                session.persist(result);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
     /**
     * Afegeix un nou exemplar.
     */
    public static Exemplar addExemplar(String codiBarres, Llibre llibre, Biblioteca biblioteca) {
        Exemplar result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                result = new Exemplar(codiBarres, llibre, biblioteca);
                session.persist(result);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
     /**
     * Afegeix una nova persona.
     */
    public static Persona addPersona(String dni, String nom, String telefon, String email) {
        Persona result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                result = new Persona(dni, nom, telefon, email);
                session.persist(result);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
    
     /**
     * Afegeix un nou prestec.
     */
    public static Prestec addPrestec(Exemplar exemplar, Persona persona, LocalDate dataPrestec, LocalDate dataRetornPrevista) {
        Prestec result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                result = new Prestec(exemplar, persona, dataPrestec, dataRetornPrevista);
                session.persist(result);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
    
     /**
     * Registrar el retorn del prestec.
     */
    public static void registrarRetornPrestec(long prestecId, LocalDate dataRetornReal) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Prestec prestec = session.get(Prestec.class, prestecId);
                if (prestec != null) {
                    prestec.setDataRetornReal(dataRetornReal);
                    session.merge(prestec);
                } else {
                }
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
    }
    
    
    /**
     * Agafar els llibres que tenen autor.
     */
    public static List<Llibre> findLlibresAmbAutors() {
        List<Llibre> result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Realizamos la consulta con un JOIN para obtener los libros con sus autores.
                String hql = "FROM Llibre l LEFT JOIN FETCH l.autors";
                Query<Llibre> query = session.createQuery(hql, Llibre.class);
                result = query.list();
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
    
    /*
    * Agafar els llibres que estan en prestec.
    */
    public static List<Object[]> findLlibresEnPrestec() {
        List<Object[]> result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Realizamos la consulta para obtener los libros que están en préstamo.
                String hql = "SELECT l, p, pr FROM Llibre l "
                    + "JOIN l.exemplars e "
                    + "JOIN e.historialPrestecs pr "
                    + "JOIN pr.persona p "
                    + "WHERE pr.actiu = :actiu";
                Query<Object[]> query = session.createQuery(hql);
                query.setParameter("actiu", true); // Filtra solo los préstamos activos
                result = query.list();
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
    
    /*
    * Agafar els llibres i les seves biblioteques.
    */
    public static List<Object[]> findLlibresAmbBiblioteques() {
        List<Object[]> result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                // Realizamos la consulta para obtener los libros y sus bibliotecas
                String hql = "SELECT l, b FROM Llibre l "
                           + "JOIN l.exemplars e "
                           + "JOIN e.biblioteca b";
                Query<Object[]> query = session.createQuery(hql);
                result = query.list();
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }
    
    /*
    * Selecciona i formteja una consulta. 
    */
    public static String formatMultipleResult(List<Object[]> results) {
        StringBuilder formattedResult = new StringBuilder();
        for (Object[] row : results) {
            if (row[0] instanceof Llibre) {
                Llibre llibre = (Llibre) row[0];
                if (row.length > 1 && row[1] instanceof Persona) {
                    Persona persona = (Persona) row[1];
                    formattedResult.append(String.format("Llibre: %s, Persona: %s\n", llibre.getTitol(), persona.getNom()));
                } else if (row.length > 1 && row[1] instanceof Biblioteca) {
                    Biblioteca biblioteca = (Biblioteca) row[1];
                    formattedResult.append(String.format("Llibre: %s, Biblioteca: %s\n", llibre.getTitol(), biblioteca.getNom()));
                }
            }
        }
        return formattedResult.toString();
    }
    
    
    
    /**
     * Obté un objecte per ID.
     */
    public static <T> T getById(Class<? extends T> clazz, long id) {
        T obj = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                obj = session.get(clazz, id);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return obj;
    }

    /**
     * Elimina una entitat per ID.
     */
    public static <T> void delete(Class<? extends T> clazz, Serializable id) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T obj = session.get(clazz, id);
                if (obj != null) {
                    session.remove(obj);
                }
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();

                throw e;
            }
        }
    }

    /**
     * Llista entitats amb filtre opcional.
     */
    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where) {
        Collection<?> result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                String hql = where.isEmpty() 
                    ? "FROM " + clazz.getName()
                    : "FROM " + clazz.getName() + " WHERE " + where;
                result = session.createQuery(hql, clazz).list();
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }

    /**
     * Executa una consulta SQL nativa d'actualització.
     */
    public static void queryUpdate(String queryString) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                session.createNativeQuery(queryString, Void.class)
                       .executeUpdate();
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
    }

    /**
     * Executa una consulta SQL nativa de selecció.
     */
    public static List<Object[]> queryTable(String queryString) {
        List<Object[]> result = null;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                NativeQuery<Object[]> query = session.createNativeQuery(queryString, Object[].class);
                result = query.getResultList();
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                throw e;
            }
        }
        return result;
    }

    /**
     * Converteix els resultats d'una consulta SQL a String.
     */
    public static String tableToString(List<Object[]> rows) {
        if (rows == null || rows.isEmpty()) {
            return "";
        }
        
        StringBuilder txt = new StringBuilder();
        for (Object[] row : rows) {
            for (Object cell : row) {
                txt.append(cell != null ? cell.toString() : "null").append(", ");
            }
            if (txt.length() >= 2) {
                txt.setLength(txt.length() - 2);  // Eliminem l'última coma i espai
            }
            txt.append("\n");
        }
        if (txt.length() >= 1) {
            txt.setLength(txt.length() - 1);  // Eliminem l'últim salt de línia
        }
        return txt.toString();
    }

    /**
     * Converteix una col·lecció d'entitats a String.
     */
    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        
        StringBuilder txt = new StringBuilder();
        for (Object obj : collection) {
            T cObj = clazz.cast(obj);
            txt.append("\n").append(cObj.toString());
        }
        if (txt.length() > 0) {
            txt.delete(0, 1);  // Eliminem el primer salt de línia
        }
        return txt.toString();
    }
}

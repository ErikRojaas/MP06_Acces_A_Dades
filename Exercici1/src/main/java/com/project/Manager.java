package com.project;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;


public class Manager {
    private static SessionFactory factory;
    
    public static void createSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            configuration.addAnnotatedClass(Ciutat.class);
            configuration.addAnnotatedClass(Ciutada.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    
    public static void close() {
        factory.close();
    }
    
    public static Ciutat addCiutat(String nom, String pais, int poblacio) {
        Session session = factory.openSession();
        Transaction tx = null;
        Ciutat result = null;
        
        try {
            tx = session.beginTransaction();
            result = new Ciutat(nom, pais, poblacio);
            session.persist(result);
            tx.commit();
        
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
            result = null;
        } finally {
            session.close(); 
        }
        return result;
    }
    
    public static Ciutada addCiutada(String nom, String cognom, int edat) {
        Session session = factory.openSession();
        Transaction tx = null;
        Ciutada result = null;
        try {
            tx = session.beginTransaction();
            result = new Ciutada(nom, cognom, edat);
            session.persist(result);
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
            result = null;
        } finally {
            session.close(); 
        }
        return result;
    }
    
    
    public static void updateCiutada(long ciutadaId, String nom, String cognom, int edat) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Ciutada obj = (Ciutada) session.get(Ciutada.class, ciutadaId);
            obj.setNom(nom);
            obj.setCognom(cognom);
            obj.setEdat(edat);
            session.merge(obj);
            tx.commit();
        
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
    }
    
    
    public static void updateCiutat(long ciutatId, String nom, String pais, int poblacio, Set<Ciutada> ciutadans) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            
            Ciutat ciutat = session.get(Ciutat.class, ciutatId);
            if (ciutat == null) {
                throw new RuntimeException("Ciutat not found with id: " + ciutatId);
            }
            
            ciutat.setNom(nom);
            ciutat.setPais(pais);
            ciutat.setPoblacio(poblacio);
            
            if (ciutat.getCiutadans() != null) {
                for (Ciutada oldCiutada : new HashSet<>(ciutat.getCiutadans())) {
                    ciutat.removeCiutada(oldCiutada);
                }
            }
            
            if (ciutadans != null) {
                for (Ciutada ciutada : ciutadans) {
                    Ciutada managedCiutada = session.get(Ciutada.class, ciutada.getCiutadaId());
                    if (managedCiutada != null) {
                        ciutat.addCiutada(managedCiutada);
                    }
                }
            }
            
            session.merge(ciutat);
            tx.commit();
            
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    public static Ciutat getCiutatWithCiutadans(long ciutatId) {
        Ciutat ciutat;
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();

            // Obtener la ciudad
            ciutat = session.get(Ciutat.class, ciutatId);

            // Inicializar la colección de ciudadanos si está marcada como lazy
            if (ciutat != null && ciutat.getCiutadans() != null) {
                // Forzamos la carga de la colección de ciudadanos
                ciutat.getCiutadans().size(); // Esto inicializa la colección
            }

            tx.commit();
        }
        return ciutat;
    }
    
    
    public static <T> T getById(Class<? extends T> clazz, long id) {
        Session session = factory.openSession();
        Transaction tx = null;
        T obj = null;
        try {
            tx = session.beginTransaction();
            obj = clazz.cast(session.get(clazz, id));
            tx.commit();
        
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
        return obj;
    }
    
    
    public static <T> void delete(Class<? extends T> clazz, Serializable id) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            // Obtener la entidad desde la base de datos
            T obj = clazz.cast(session.get(clazz, id));

            // Si la entidad existe, eliminarla
            if (obj != null) {
                session.delete(obj);  // Eliminar el objeto de la sesión
                session.flush();      // Forzar la sincronización con la base de datos
            }

            tx.commit();  // Confirmar la transacción
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();  // En caso de error, hacer rollback
            e.printStackTrace();
        } finally {
            session.close();  // Asegurarse de cerrar la sesión
        }
    }
    
    public static <T> Collection<?> listCollection(Class<? extends T> clazz) {
        return listCollection(clazz, "");
    }

    public static <T> Collection<?> listCollection(Class<? extends T> clazz, String where){
        Session session = factory.openSession();
        Transaction tx = null;
        Collection<?> result = null;
        try {
            tx = session.beginTransaction();
            if (where.length() == 0) {
                result = session.createQuery("FROM " + clazz.getName(), clazz).list(); // Consulta para obtener la colección
            } else {
                result = session.createQuery("FROM " + clazz.getName() + " WHERE " + where, clazz).list(); // Consulta con condición
            }

            // Inicializamos las colecciones lazy (si las hay)
            for (Object entity : result) {
                if (entity instanceof Ciutat) {
                    Ciutat ciutat = (Ciutat) entity;
                    // Forzamos la inicialización de la colección 'ciutadans'
                    if (ciutat.getCiutadans() != null) {
                        ciutat.getCiutadans().size(); // Esto inicializa la colección
                    }
                }
                // Aquí puedes agregar otros casos de inicialización si hay otras colecciones lazy
            }

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close(); 
        }
        return result;
    }

    public static <T> String collectionToString(Class<? extends T> clazz, Collection<?> collection){
        StringBuilder txt = new StringBuilder();
        for (Object obj : collection) {
            T cObj = clazz.cast(obj);
            // Verificamos si el objeto tiene colecciones lazy y las inicializamos
            if (cObj instanceof Ciutat) {
                Ciutat ciutat = (Ciutat) cObj;
                // Inicializamos la colección de 'ciutadans' si es lazy
                if (ciutat.getCiutadans() != null) {
                    ciutat.getCiutadans().size(); // Esto inicializa la colección
                }
            }
            txt.append("\n").append(cObj.toString());
        }

        // Limpiar el primer salto de línea si existe
        if (txt.length() > 0 && txt.charAt(0) == '\n') {
            txt.deleteCharAt(0);
        }

        return txt.toString();
    }
    
    public static void queryUpdate(String queryString) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            NativeQuery<?> query = session.createNativeQuery(queryString, Void.class); // Updated to NativeQuery
            query.executeUpdate();
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
    }
    
    public static List<Object[]> queryTable(String queryString) {
        Session session = factory.openSession();
        Transaction tx = null;
        List<Object[]> result = null;
        try {
            tx = session.beginTransaction();
            NativeQuery<Object[]> query = session.createNativeQuery(queryString, Object[].class); // Updated to NativeQuery
            result = query.getResultList(); // Changed from list() to getResultList()
            tx.commit();
        } catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace(); 
        } finally {
            session.close(); 
        }
        return result;
    }
    
    public static String tableToString(List<Object[]> rows) {
        String txt = "";
        for (Object[] row : rows) {
            for (Object cell : row) {
                txt += cell.toString() + ", ";
            }
            if (txt.length() >= 2 && txt.substring(txt.length() - 2).compareTo(", ") == 0) {
                txt = txt.substring(0, txt.length() - 2);
            }
            txt += "\n";
        }
        if (txt.length() >= 2) {
            txt = txt.substring(0, txt.length() - 1);
        }
        return txt;
    }

}

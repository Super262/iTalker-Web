package net.qiujuer.web.italker.push.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.logging.Level;
import java.util.logging.Logger;
public class Hib {
    private static final Logger LOGGER = Logger.getLogger(Hib.class.getName());
    private static SessionFactory sessionFactory;

    static {
        init();
    }

    private static void init() {

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();

            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public static void setup() {
        LOGGER.log(Level.INFO, "Hibernate setup succeed!");
    }


    public static SessionFactory sessionFactory() {
        return sessionFactory;
    }


    public static Session session() {
        return sessionFactory.getCurrentSession();
    }

    public static void closeFactory() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }



    public interface QueryOnly {
        void query(Session session);
    }

    public static void queryOnly(QueryOnly query) {
        Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();

        try {
            query.query(session);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }
        } finally {
            session.close();
        }
    }

    public interface Query<T> {
        T query(Session session);
    }
    public static <T> T query(Query<T> query) {
        Session session = sessionFactory.openSession();
        final Transaction transaction = session.beginTransaction();

        T t = null;
        try {
            t = query.query(session);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                transaction.rollback();
            } catch (RuntimeException e1) {
                e1.printStackTrace();
            }
        } finally {
            session.close();
        }

        return t;
    }


}

package ua.pp.myshko.csvholder.utils;

import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;

/**
 * @author M. Chernenko
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory() {

        if ((sessionFactory == null) || (sessionFactory.isClosed())) {
            Configuration configuration = new Configuration().configure();
            configuration.addResource("mapping.xml");
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(builder.build());
        }
        return sessionFactory;
    }

    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    public static void close() {
        getSessionFactory().close();
    }

}

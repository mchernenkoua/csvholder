package ua.pp.myshko.csvholder.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ua.pp.myshko.csvholder.CSVHolderException;
import ua.pp.myshko.csvholder.model.FileDescription;
import ua.pp.myshko.csvholder.services.EntityMapper;
import ua.pp.myshko.csvholder.services.impl.EntityMapperImpl;

/**
 * @author M. Chernenko
 */
public class HibernateUtil {

    public static final String MAPPING_FILE = "mapping.xml";
    private static SessionFactory sessionFactory;
    private static EntityMapper entityMapper = new EntityMapperImpl();

    public static synchronized SessionFactory getSessionFactory() {

        if ((sessionFactory == null) || (sessionFactory.isClosed())) {
            Configuration configuration = new Configuration().configure();
            configuration.addResource(MAPPING_FILE);
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

    public static void updateEntityMapping(FileDescription fileDescription) throws CSVHolderException {
        // TODO: read mapping.xml
        // TODO: find fileDescription.tableName table
        // TODO: check columns and update mapping.xml if needed
        String fullMappingPath = HibernateUtil.class.getClassLoader().getResource(MAPPING_FILE).getPath();
        entityMapper.saveMapping(fullMappingPath, fileDescription.getTableName(), fileDescription.getColumns());
        // TODO: close session factory if was changes
        if ((sessionFactory != null) && (!sessionFactory.isClosed())) {
            sessionFactory.close();
        }
    }
}

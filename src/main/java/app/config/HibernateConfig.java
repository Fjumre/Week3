package app.config;

import app.model.Product;
import app.model.Role;
import app.model.User;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateConfig {
    private static EntityManagerFactory emf;
    private static boolean isIntegrationTest = false; // used for test profile

    // ====== public API ======
    public static void setTestMode(boolean isTest) { HibernateConfig.isIntegrationTest = isTest; }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            if (System.getenv("DEPLOYED") != null) {
                emf = buildEntityFactoryConfigDeployed();
            } else {
                emf = createEMF(false);
            }
        }
        return emf;
    }

    // Keep a separate EMF for tests if you use Testcontainers
    private static EntityManagerFactory emfTest;
    public static EntityManagerFactory getEntityManagerFactoryForTest() {
        if (emfTest == null) {
            emfTest = createEMF(true);
        }
        return emfTest;
    }

    // ====== internals ======
    private static EntityManagerFactory buildEntityFactoryConfigDeployed() {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            // --- Deployed: read from env ---
            // Expected envs (set them on your host/container):
            // DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
            String host = envOr("DB_HOST", "localhost");
            String port = envOr("DB_PORT", "3306");
            String db   = envOr("DB_NAME", getDBName()); // fallback to properties file
            String user = envOr("DB_USER", "root");
            String pass = envOr("DB_PASSWORD", "");

            props.put("hibernate.connection.url",
                    "jdbc:mysql://" + host + ":" + port + "/" + db
                            + "?useUnicode=true&characterEncoding=utf8"
                            + "&serverTimezone=UTC"
                            + "&useSSL=false"
                            + "&allowPublicKeyRetrieval=true");
            props.put("hibernate.connection.username", user);
            props.put("hibernate.connection.password", pass);

            // MySQL specifics
            props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            props.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");

            // Never let deployed instances mutate schema automatically
            props.put("hibernate.hbm2ddl.auto", "validate");

            // Common
            props.put("hibernate.archive.autodetection", "class");
            props.put("hibernate.current_session_context_class", "thread");
            props.put("hibernate.show_sql", "false");
            props.put("hibernate.format_sql", "false");

            return buildEMF(configuration, props);
        } catch (Throwable ex) {
            System.err.println("EMF creation failed (deployed): " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory createEMF(boolean forTest) {
        try {
            Configuration configuration = new Configuration();
            Properties props = new Properties();

            if (forTest || isIntegrationTest) {
                setTestProperties(props);  // keeps your Postgres Testcontainers profile
            } else {
                setDevProperties(props);   // local MySQL dev profile
            }

            return buildEMF(configuration, props);
        } catch (Throwable ex) {
            System.err.println("EMF creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    private static EntityManagerFactory buildEMF(Configuration configuration, Properties props) {
        configuration.setProperties(props);
        addAnnotatedEntities(configuration);
        ServiceRegistry sr = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();
        SessionFactory sf = configuration.buildSessionFactory(sr);
        return sf.unwrap(EntityManagerFactory.class);
    }

    private static void addAnnotatedEntities(Configuration configuration) {
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Role.class);
        configuration.addAnnotatedClass(Product.class);
    }

    private static String getDBName() {
        // if you keep this properties file, it can still provide a fallback db name
        return Utils.getPropertyValue("db.name", "properties-from-pom.properties");
    }

    private static void setDevProperties(Properties props) {
        // ---- Local MySQL dev ----
        String host = "localhost";
        String port = "3306";
        String db   = envOr("DB_NAME", getDBName()); // e.g., "dbc"
        String user = envOr("DB_USER", "root");
        String pass = envOr("DB_PASSWORD", "MyNewPassword123!"); // <- set to your local password

        props.put("hibernate.connection.url",
                "jdbc:mysql://" + host + ":" + port + "/" + db
                        + "?useUnicode=true&characterEncoding=utf8"
                        + "&serverTimezone=UTC"
                        + "&useSSL=false"
                        + "&allowPublicKeyRetrieval=true");
        props.put("hibernate.connection.username", user);
        props.put("hibernate.connection.password", pass);

        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");

        // IMPORTANT: we imported the schema manually; do not auto-create/alter/drop
        props.put("hibernate.hbm2ddl.auto", "validate");

        props.put("hibernate.current_session_context_class", "thread");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.format_sql", "false");
        props.put("hibernate.use_sql_comments", "false");
    }


    //to align tests with MySQL, swap this to a MySQL Testcontainer URL.
    private static void setTestProperties(Properties props) {
        // ---- Existing Postgres Testcontainers profile ----
        props.put("hibernate.connection.driver_class", "org.testcontainers.jdbc.ContainerDatabaseDriver");
        props.put("hibernate.connection.url", "jdbc:tc:postgresql:15.3-alpine3.18:///test_db");
        props.put("hibernate.connection.username", "postgres");
        props.put("hibernate.connection.password", "postgres");
        props.put("hibernate.archive.autodetection", "class");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    }

    private static String envOr(String key, String defaultVal) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? defaultVal : v;
    }
}

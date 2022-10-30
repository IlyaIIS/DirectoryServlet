package database;

import accounts.UserProfile;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInitListener implements ServletContextListener {
    private static SessionFactory sessionFactory;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
            configuration.setProperty("hibernate.connection.driver_cl", "com.mysql.jdbc.Driver");
            configuration.setProperty("hibernate.connection.url", "jdbc:mysql://85.209.2.189:3306/hibernate_db");
            configuration.setProperty("hibernate.connection.username", "root");
            configuration.setProperty("hibernate.connection.password", "****");
            configuration.setProperty("hibernate.hbm2ddl.auto", "validate");

            configuration.addAnnotatedClass(UserProfile.class);

            sessionFactory = createSessionFactory(configuration);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    public static Session getNewSession() {
        return sessionFactory.openSession();
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
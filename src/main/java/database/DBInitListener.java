package database;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DBInitListener implements ServletContextListener {

    public static QueryExecutor queryExecutor;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        queryExecutor = new QueryExecutor(DBService.getNewMysqlConnection());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

}
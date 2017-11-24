package bsds.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

// Connects to the AWS RDS MySQL Server
public class ConnectionManager {

	/*
	* AWS credentials for remote login
	* Command :
	* > mysql -h skieresort.cv4laudu9jlm.us-west-2.rds.amazonaws.com -P 3306 -u Administrator -p
	* MySQL Commands to view the records :
	*  USE ski;
	*  SELECT * FROM Record;
	*  SELECT * FROM Record WHERE skierID = {skierID} AND DayNum = {DayNum};
	* */

    // Credentials for my instance of RDS on AWS
    private static final String ENDPOINT = "skieresort.cv4laudu9jlm.us-west-2.rds.amazonaws.com";
    private static final String PORT = "3306";

    private static final String DB = "skier";
    public static final String USER = "Administrator";
    public static final String PASSWORD = "helloworld";

    public static final String URL = "jdbc:mysql://" + ENDPOINT + ":" + PORT + "/" + DB + "?user=" + USER
            + "&password=" + PASSWORD;


    private final DataSource datasource;

    // Establish connection by creating the URL
    public ConnectionManager() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setUrl(URL);
        poolProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
        poolProperties.setJmxEnabled(true);
        poolProperties.setTestWhileIdle(false);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setTestOnReturn(false);
        poolProperties.setDefaultAutoCommit(true);
        poolProperties.setValidationInterval(30000);
        poolProperties.setTimeBetweenEvictionRunsMillis(30000);
        poolProperties.setMaxActive(32);
        poolProperties.setInitialSize(32);
        poolProperties.setMaxWait(10000);
        poolProperties.setRemoveAbandonedTimeout(60);
        poolProperties.setMinEvictableIdleTimeMillis(30000);
        poolProperties.setMinIdle(4);
        poolProperties.setLogAbandoned(true);
        poolProperties.setRemoveAbandoned(true);
        poolProperties.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        datasource = new DataSource();
        datasource.setPoolProperties(poolProperties);
    }

    public Connection getConnection() throws SQLException {
        try {
            return datasource.getConnection();
        } catch (SQLException e) {
            throw new SQLException();
        }
    }

    public static Connection makeConnection(String jdbcUrl, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Close the connection to the database instance.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

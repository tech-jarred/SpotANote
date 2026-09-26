package com.spotanote;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBExplorer {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/SpotANote";
    private static final String DB_USER = "spotanote_user";
    private static final String DB_PASSWORD = "password";

    private static final HikariDataSource dataSource;

    /**
     * Create a shared pool that will hold at least 2 connections to the database at all times, and can open up to 10 if needed
     */
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);

        // Pool Settings
        config.setMaximumPoolSize(10);        // Max open connections
        config.setMinimumIdle(2);             // Minimum idle connections
        config.setIdleTimeout(30000);         // 30 seconds
        config.setConnectionTimeout(10000);   // 10 second wait timeout
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");

        dataSource = new HikariDataSource(config);
    }

    // Make the constructor private so no instances of this class can be made. Only need to invoke class name and method.
    private DBExplorer() {}

    /**
     * Obtains a connection from the pool.
     * Must be used within a try-with-resources block!
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
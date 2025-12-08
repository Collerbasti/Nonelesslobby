package Mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

/**
 * MySQL connection manager using HikariCP for connection pooling.
 * Provides high-performance database connections with automatic connection management.
 */
public class MySQL {
    // These values should ideally be loaded from a config file
    public static String host = "ms2778.gamedata.io";
    public static String port = "3306";
    public static String database = "ni506153_1_DB";
    public static String username = "ni506153_1_DB";
    public static String password = "y77ei7XP";

    private static HikariDataSource dataSource;

    private static Logger logger() {
        return Bukkit.getLogger();
    }

    public static synchronized void connect() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }
        
        try {
            logger().info("[Lobby-MySQL] Initialisiere Verbindungspool...");
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
            
            // Connection pool settings for optimal performance
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setLeakDetectionThreshold(60000);
            
            // MySQL optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(config);
            logger().info("[Lobby-MySQL] Verbindungspool erstellt.");
        } catch (Exception e) {
            logger().severe("[Lobby-MySQL] Fehler beim Erstellen des Pools: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                dataSource.close();
                dataSource = null;
                logger().info("[Lobby-MySQL] Verbindungspool geschlossen.");
            } catch (Exception e) {
                logger().severe("[Lobby-MySQL] Fehler beim Schließen des Pools: " + e.getMessage());
            }
        }
    }

    public static synchronized boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    /**
     * Gets a connection from the pool.
     * Connections must be closed after use to return them to the pool.
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            connect();
        }
        return dataSource.getConnection();
    }
}




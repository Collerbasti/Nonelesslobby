package Mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

/**
 * Einfache MySQL-Verwaltung für das Lobby-Plugin.
 * Hinweis: Zugangsdaten sind aktuell identisch zu MiniCardGame, damit beide dieselbe DB/Tabelle nutzen.
 */
public class MySQL {
    // Diese Werte sollten idealerweise aus einer Config geladen werden.
    // Für die Konsistenz mit MiniCardGame nutzen wir hier identische Defaults.
    public static String host = "ms2778.gamedata.io";
    public static String port = "3306";
    public static String database = "ni506153_1_DB";
    public static String username = "ni506153_1_DB";
    public static String password = "y77ei7XP";

    private static Connection connection;

    private static Logger logger() {
        return Bukkit.getLogger();
    }

    public static synchronized void connect() {
        if (isConnected()) {
            return;
        }
        try {
            logger().info("[Lobby-MySQL] Verbinde zur Datenbank...");
            connection = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false",
                username,
                password
            );
            logger().info("[Lobby-MySQL] Verbindung hergestellt.");
        } catch (SQLException e) {
            logger().severe("[Lobby-MySQL] Verbindung fehlgeschlagen: " + e.getMessage());
        }
    }

    public static synchronized void disconnect() {
        if (!isConnected()) {
            return;
        }
        try {
            connection.close();
            connection = null;
            logger().info("[Lobby-MySQL] Verbindung geschlossen.");
        } catch (SQLException e) {
            logger().severe("[Lobby-MySQL] Fehler beim Schließen der Verbindung: " + e.getMessage());
        }
    }

    public static synchronized boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }
}




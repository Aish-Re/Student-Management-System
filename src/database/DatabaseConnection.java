package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages the singleton JDBC database connection for the Student Management System.
 *
 * <p>Security Notice: Database credentials are never hardcoded in source control.
 * Credentials are dynamically resolved in the following priority:
 * 1. Environment variable: DB_PASSWORD
 * 2. JVM system property: -Ddb.password=...
 * 3. Classpath resource: /db.properties
 * 4. Project root / code directory: db.properties
 * 5. Current working directory: db.properties</p>
 */
public class DatabaseConnection {

    private static final String URL     = "jdbc:mysql://localhost:3306/student_management_system"
                                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";

    private static Connection connection = null;

    /**
     * Resolves the MySQL password from environment variables, system properties,
     * the classpath, or local configuration files.
     *
     * @return the resolved password string, or empty string if not configured
     */
    private static String getPassword() {
        // 1. Check environment variable
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null && !envPass.trim().isEmpty()) {
            return envPass.trim();
        }

        // 2. Check JVM system property (-Ddb.password=...)
        String propPass = System.getProperty("db.password");
        if (propPass != null && !propPass.trim().isEmpty()) {
            return propPass.trim();
        }

        // 3. Check classpath resource (/db.properties)
        try (InputStream is = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String p = props.getProperty("db.password");
                if (p != null) return p.trim();
            }
        } catch (Exception ignored) {}

        try (InputStream is = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String p = props.getProperty("db.password");
                if (p != null) return p.trim();
            }
        } catch (Exception ignored) {}

        // 4. Check directory relative to the compiled code location (project root)
        try {
            URL codeLocation = DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation();
            if (codeLocation != null) {
                File loc = new File(codeLocation.toURI());
                File dir = loc.isDirectory() ? loc : loc.getParentFile();

                // Check in same directory
                File f1 = new File(dir, "db.properties");
                if (f1.exists()) {
                    String p = loadPasswordFromFile(f1);
                    if (p != null) return p;
                }

                // Check in parent directory (e.g. project root when running from bin/)
                File f2 = new File(dir.getParentFile(), "db.properties");
                if (f2.exists()) {
                    String p = loadPasswordFromFile(f2);
                    if (p != null) return p;
                }
            }
        } catch (Exception ignored) {}

        // 5. Check current working directory
        File cwdFile = new File("db.properties");
        if (cwdFile.exists()) {
            String p = loadPasswordFromFile(cwdFile);
            if (p != null) return p;
        }

        return "";
    }

    private static String loadPasswordFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(fis);
            String p = props.getProperty("db.password");
            if (p != null) return p.trim();
        } catch (Exception e) {
            System.err.println("[DB CONFIG] Could not read " + file.getAbsolutePath() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns an open database connection using standard JDBC DriverManager.
     * Automatically establishes or reconnects if closed.
     *
     * @return active java.sql.Connection instance, or null if connection failed
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, DB_USER, getPassword());
                System.out.println("Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] MySQL JDBC Driver not found. "
                + "Ensure mysql-connector-j-9.7.0.jar is on the classpath.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection failed.");
            System.err.println("  URL      : " + URL);
            System.err.println("  User     : " + DB_USER);
            System.err.println("  SQLState : " + e.getSQLState());
            System.err.println("  Code     : " + e.getErrorCode());
            System.err.println("  Message  : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Closes the active connection and resets the singleton reference.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Failed to close connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns true if a live, non-closed connection can be obtained.
     *
     * @return true if database is reachable, false otherwise
     */
    public static boolean testConnection() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection test failed: " + e.getMessage());
            return false;
        }
    }
}

package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseConnection {

    private static final String URL     = "jdbc:mysql://localhost:3306/student_management_system"
                                        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";

    private static Connection connection = null;

    private static String getPassword() {
    
        String envPass = System.getenv("DB_PASSWORD");
        if (envPass != null && !envPass.trim().isEmpty()) {
            return envPass.trim();
        }

        String propPass = System.getProperty("db.password");
        if (propPass != null && !propPass.trim().isEmpty()) {
            return propPass.trim();
        }

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

        try {
            URL codeLocation = DatabaseConnection.class.getProtectionDomain().getCodeSource().getLocation();
            if (codeLocation != null) {
                File loc = new File(codeLocation.toURI());
                File dir = loc.isDirectory() ? loc : loc.getParentFile();

          
                File f1 = new File(dir, "db.properties");
                if (f1.exists()) {
                    String p = loadPasswordFromFile(f1);
                    if (p != null) return p;
                }

                
                File f2 = new File(dir.getParentFile(), "db.properties");
                if (f2.exists()) {
                    String p = loadPasswordFromFile(f2);
                    if (p != null) return p;
                }
            }
        } catch (Exception ignored) {}

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

import ui.LoginUI;
import database.DatabaseConnection;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application entry point for the Student Management System.
 * Initializes the system Look and Feel, launches the Login UI on the Swing
 * Event Dispatch Thread, and registers a JVM shutdown hook to close JDBC connections.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseConnection.closeConnection();
        }));
    }
}

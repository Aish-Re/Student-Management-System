package service;

import dao.AdminDAO;

/**
 * AuthService handles authentication logic for the Student Management System.
 *
 * <p>This service wraps AdminDAO to keep authentication logic out of the UI layer.
 * LoginUI currently calls AdminDAO directly and continues to work unchanged.
 * This class is provided as a clean service-layer entry point for authentication.</p>
 */
public class AuthService {

    private final AdminDAO adminDAO;

    public AuthService() {
        this.adminDAO = new AdminDAO();
    }

    /**
     * Validates admin credentials against the database.
     *
     * @param username the entered username (trimmed before use)
     * @param password the entered password
     * @return true if credentials match a record in the admin table, false otherwise
     */
    public boolean login(String username, String password) {
        if (!areCredentialsNonEmpty(username, password)) {
            System.err.println("[AuthService] Login rejected: one or both fields are empty.");
            return false;
        }
        return adminDAO.validateAdmin(username.trim(), password);
    }

    /**
     * Checks whether both credential fields are non-null and non-empty.
     *
     * @param username the username input
     * @param password the password input
     * @return true if both are non-empty
     */
    public boolean areCredentialsNonEmpty(String username, String password) {
        return username != null && !username.trim().isEmpty()
            && password != null && !password.isEmpty();
    }
}

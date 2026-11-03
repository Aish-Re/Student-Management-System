package service;

import dao.AdminDAO;

public class AuthService {

    private final AdminDAO adminDAO;

    public AuthService() {
        this.adminDAO = new AdminDAO();
    }

    public boolean login(String username, String password) {
        if (!areCredentialsNonEmpty(username, password)) {
            System.err.println("[AuthService] Login rejected: one or both fields are empty.");
            return false;
        }
        return adminDAO.validateAdmin(username.trim(), password);
    }

    public boolean areCredentialsNonEmpty(String username, String password) {
        return username != null && !username.trim().isEmpty()
            && password != null && !password.isEmpty();
    }
}

package service;

import java.util.regex.Pattern;

/**
 * Utility service providing input validation methods for student and authentication forms.
 */
public class ValidationService {

    /** Validates standard email format (user@domain.ext). */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && Pattern.matches(emailRegex, email);
    }

    /** Validates 10-digit mobile number starting with digits 6-9. */
    public static boolean isValidPhoneNumber(String phone) {
        String phoneRegex = "^[6-9]\\d{9}$";
        return phone != null && Pattern.matches(phoneRegex, phone);
    }

    /** Validates student ID format (e.g. STU001, STU042). */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && studentId.matches("^STU\\d{3}$");
    }

    /** Validates that full name contains only alphabetical characters and spaces (min 2 chars). */
    public static boolean isValidName(String name) {
        return name != null && name.trim().length() >= 2 && name.matches("^[a-zA-Z\\s]+$");
    }

    /** Checks whether a string value is null or empty after trimming. */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** Validates academic semester range between 1 and 8. */
    public static boolean isValidSemester(int semester) {
        return semester >= 1 && semester <= 8;
    }
}

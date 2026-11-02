package dao;

import database.DatabaseConnection;
import model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Student CRUD operations and reporting queries.
 * Employs parameterized PreparedStatement queries and try-with-resources
 * to guarantee SQL injection safety and cursor resource closure.
 */
public class StudentDAO {

    /**
     * Inserts a new student record into the MySQL database.
     */
    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (student_id, full_name, gender, date_of_birth, "
                     + "email, phone_number, address, course, department, semester, enrollment_date) "
                     + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getGender());
            pstmt.setDate(4, new java.sql.Date(student.getDateOfBirth().getTime()));
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhoneNumber());
            pstmt.setString(7, student.getAddress());
            pstmt.setString(8, student.getCourse());
            pstmt.setString(9, student.getDepartment());
            pstmt.setInt(10, student.getSemester());
            pstmt.setDate(11, new java.sql.Date(student.getEnrollmentDate().getTime()));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to add student ID '" + student.getStudentId() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all student records ordered alphabetically by full name.
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY full_name";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return students;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to retrieve all students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Searches student records matching ID, full name, or email pattern.
     */
    public List<Student> searchStudents(String searchTerm) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students WHERE student_id LIKE ? OR full_name LIKE ? OR email LIKE ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return students;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(extractStudentFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to search students with term '" + searchTerm + "': " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Updates an existing student record identified by student ID.
     */
    public boolean updateStudent(Student student) {
        String query = "UPDATE students SET full_name=?, gender=?, date_of_birth=?, email=?, "
                     + "phone_number=?, address=?, course=?, department=?, semester=?, enrollment_date=? "
                     + "WHERE student_id=?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getGender());
            pstmt.setDate(3, new java.sql.Date(student.getDateOfBirth().getTime()));
            pstmt.setString(4, student.getEmail());
            pstmt.setString(5, student.getPhoneNumber());
            pstmt.setString(6, student.getAddress());
            pstmt.setString(7, student.getCourse());
            pstmt.setString(8, student.getDepartment());
            pstmt.setInt(9, student.getSemester());
            pstmt.setDate(10, new java.sql.Date(student.getEnrollmentDate().getTime()));
            pstmt.setString(11, student.getStudentId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to update student ID '" + student.getStudentId() + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a student record by primary key student ID.
     */
    public boolean deleteStudent(String studentId) {
        String query = "DELETE FROM students WHERE student_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to delete student ID '" + studentId + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fetches a single student record matching the specified student ID.
     */
    public Student getStudentById(String studentId) {
        String query = "SELECT * FROM students WHERE student_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return null;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractStudentFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to fetch student by ID '" + studentId + "': " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if a student record with the given ID already exists.
     */
    public boolean isStudentIdExists(String studentId) {
        String query = "SELECT 1 FROM students WHERE student_id = ?";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return false;

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, studentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to check existence of student ID '" + studentId + "': " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the aggregate count of all registered students.
     */
    public int getTotalStudentsCount() {
        String query = "SELECT COUNT(*) FROM students";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to get total student count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Retrieves the 5 most recently registered students based on enrollment date.
     */
    public List<Student> getRecentStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students ORDER BY enrollment_date DESC LIMIT 5";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return students;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                students.add(extractStudentFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to retrieve recent students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Groups student counts by distinct course names.
     */
    public List<Object[]> getCourseStatistics() {
        List<Object[]> stats = new ArrayList<>();
        String query = "SELECT course, COUNT(*) as count FROM students GROUP BY course";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return stats;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] stat = new Object[2];
                stat[0] = rs.getString("course");
                stat[1] = rs.getInt("count");
                stats.add(stat);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to retrieve course statistics: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Returns the number of distinct active courses represented in student records.
     */
    public int getActiveCoursesCount() {
        String query = "SELECT COUNT(DISTINCT course) FROM students";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to retrieve active courses count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns the number of distinct academic departments represented in student records.
     */
    public int getDepartmentsCount() {
        String query = "SELECT COUNT(DISTINCT department) FROM students";

        Connection conn = DatabaseConnection.getConnection();
        if (conn == null) return 0;

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Failed to retrieve departments count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Maps the current row of a ResultSet to a populated Student model instance.
     */
    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getString("student_id"));
        student.setFullName(rs.getString("full_name"));
        student.setGender(rs.getString("gender"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setEmail(rs.getString("email"));
        student.setPhoneNumber(rs.getString("phone_number"));
        student.setAddress(rs.getString("address"));
        student.setCourse(rs.getString("course"));
        student.setDepartment(rs.getString("department"));
        student.setSemester(rs.getInt("semester"));
        student.setEnrollmentDate(rs.getDate("enrollment_date"));
        return student;
    }
}

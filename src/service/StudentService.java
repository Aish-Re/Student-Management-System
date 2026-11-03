package service;

import model.Student;
import dao.StudentDAO;
import java.util.List;

public class StudentService {
    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    public boolean addStudent(Student student) {
        if (!validateStudent(student)) {
            return false;
        }
        if (studentDAO.isStudentIdExists(student.getStudentId())) {
            return false;
        }
        return studentDAO.addStudent(student);
    }

    public boolean updateStudent(Student student) {
        if (!validateStudent(student)) {
            return false;
        }
        return studentDAO.updateStudent(student);
    }

    public boolean deleteStudent(String studentId) {
        return studentDAO.deleteStudent(studentId);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String searchTerm) {
        return studentDAO.searchStudents(searchTerm);
    }

    public Student getStudentById(String studentId) {
        return studentDAO.getStudentById(studentId);
    }

    public int getTotalStudentsCount() {
        return studentDAO.getTotalStudentsCount();
    }

    public List<Student> getRecentStudents() {
        return studentDAO.getRecentStudents();
    }

    public List<Object[]> getCourseStatistics() {
        return studentDAO.getCourseStatistics();
    }

    public int getActiveCoursesCount() {
        return studentDAO.getActiveCoursesCount();
    }

    public int getDepartmentsCount() {
        return studentDAO.getDepartmentsCount();
    }

    /**
     * Enforces required field, regex format, and range validations before database persistence.
     */
    private boolean validateStudent(Student student) {
        if (ValidationService.isEmpty(student.getStudentId()) ||
            ValidationService.isEmpty(student.getFullName()) ||
            ValidationService.isEmpty(student.getEmail()) ||
            ValidationService.isEmpty(student.getPhoneNumber())) {
            return false;
        }

        if (!ValidationService.isValidStudentId(student.getStudentId())) {
            return false;
        }

        if (!ValidationService.isValidEmail(student.getEmail())) {
            return false;
        }

        if (!ValidationService.isValidPhoneNumber(student.getPhoneNumber())) {
            return false;
        }

        if (!ValidationService.isValidName(student.getFullName())) {
            return false;
        }

        if (!ValidationService.isValidSemester(student.getSemester())) {
            return false;
        }

        return true;
    }
}

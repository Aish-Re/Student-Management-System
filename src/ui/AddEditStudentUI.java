package ui;

import model.Student;
import service.StudentService;
import service.ValidationService;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Date;

/**
 * Modal dialog for registering a new student or editing an existing student record.
 * Form values are validated before submission.
 */
public class AddEditStudentUI extends JDialog {

    private static final Color PRIMARY    = new Color(41, 128, 185);
    private static final Color FIELD_BDR  = new Color(200, 210, 220);
    private static final Color LABEL_CLR  = new Color(50,  60,  70);
    private static final Color SECTION_BG = new Color(245, 248, 252);

    private StudentService studentService;
    private Student        editingStudent;
    private JFrame         parent;

    private JTextField     studentIdField, nameField, emailField, phoneField, addressField;
    private JComboBox<String> genderCombo, courseCombo, departmentCombo;
    private JSpinner       semesterSpinner, dobSpinner, enrollmentSpinner;

    public AddEditStudentUI(JFrame parent, Student student) {
        super(parent, student == null ? "Add Student" : "Edit Student", true);
        this.parent         = parent;
        this.editingStudent = student;
        this.studentService = new StudentService();
        initComponents();
        if (student != null) loadStudentData();
    }

    private void initComponents() {
        setSize(560, 680);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY);
        titleBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLbl = new JLabel(editingStudent == null ? "  Add New Student" : "  Edit Student");
        titleLbl.setFont(new Font("Arial", Font.BOLD, 18));
        titleLbl.setForeground(Color.WHITE);
        titleBar.add(titleLbl, BorderLayout.WEST);

        JLabel subLbl = new JLabel(editingStudent == null
            ? "Fill all required fields (*)" : "Update student details");
        subLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        subLbl.setForeground(new Color(200, 220, 240));
        titleBar.add(subLbl, BorderLayout.EAST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(16, 24, 8, 24));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        int row = 0;

        row = addSectionHeader(form, g, row, "Personal Information");

        studentIdField = new JTextField();
        studentIdField.setEnabled(editingStudent == null);
        if (editingStudent == null)
            studentIdField.setToolTipText("Format: STU001, STU002, …");
        row = addFormRow(form, g, row, "Student ID *", studentIdField);

        nameField = new JTextField();
        row = addFormRow(form, g, row, "Full Name *", nameField);

        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        row = addFormRow(form, g, row, "Gender *", genderCombo);

        SpinnerDateModel dobModel = new SpinnerDateModel();
        dobSpinner = new JSpinner(dobModel);
        dobSpinner.setEditor(new JSpinner.DateEditor(dobSpinner, "yyyy-MM-dd"));
        row = addFormRow(form, g, row, "Date of Birth *", dobSpinner);

        emailField = new JTextField();
        row = addFormRow(form, g, row, "Email *", emailField);

        phoneField = new JTextField();
        phoneField.setToolTipText("10 digits, starting with 6–9");
        row = addFormRow(form, g, row, "Phone Number *", phoneField);

        addressField = new JTextField();
        row = addFormRow(form, g, row, "Address", addressField);

        row = addSectionHeader(form, g, row, "Academic Information");

        courseCombo = new JComboBox<>(new String[]{
            "Computer Science", "Information Technology",
            "Electronics", "Mechanical", "Civil"});
        row = addFormRow(form, g, row, "Course *", courseCombo);

        departmentCombo = new JComboBox<>(new String[]{
            "Software Engineering", "Web Development",
            "Data Science", "AI/ML", "Networking"});
        row = addFormRow(form, g, row, "Department *", departmentCombo);

        semesterSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 8, 1));
        row = addFormRow(form, g, row, "Semester *  (1–8)", semesterSpinner);

        SpinnerDateModel enrollModel = new SpinnerDateModel(
            new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        enrollmentSpinner = new JSpinner(enrollModel);
        enrollmentSpinner.setEditor(new JSpinner.DateEditor(enrollmentSpinner, "yyyy-MM-dd"));
        row = addFormRow(form, g, row, "Enrollment Date *", enrollmentSpinner);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 12));
        btnRow.setBackground(Color.WHITE);
        btnRow.setBorder(new MatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

        JButton saveBtn   = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        ButtonStyler.styleSuccessButton(saveBtn);
        ButtonStyler.styleDangerButton(cancelBtn);
        saveBtn.setPreferredSize(new Dimension(120, 38));
        cancelBtn.setPreferredSize(new Dimension(120, 38));

        saveBtn.addActionListener(e   -> saveStudent());
        cancelBtn.addActionListener(e -> dispose());

        btnRow.add(saveBtn);
        btnRow.add(cancelBtn);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        JPanel root = new JPanel(new BorderLayout());
        root.add(titleBar, BorderLayout.NORTH);
        root.add(scroll,   BorderLayout.CENTER);
        root.add(btnRow,   BorderLayout.SOUTH);
        add(root);
    }

    /**
     * Adds a stylized section header spanning both columns of the form.
     */
    private int addSectionHeader(JPanel form, GridBagConstraints g, int row, String title) {
        JLabel lbl = new JLabel("  " + title);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(PRIMARY);
        lbl.setOpaque(true);
        lbl.setBackground(SECTION_BG);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 3, 0, 0, PRIMARY),
            BorderFactory.createEmptyBorder(5, 8, 5, 0)
        ));

        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.insets = new Insets(12, 0, 6, 0);
        form.add(lbl, g);
        g.gridwidth = 1;
        return row + 1;
    }

    /**
     * Adds a field row with a uniform label and styled component.
     */
    private int addFormRow(JPanel form, GridBagConstraints g,
                           int row, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(LABEL_CLR);
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        g.weightx = 0.32;
        g.insets = new Insets(4, 2, 4, 8);
        form.add(lbl, g);

        field.setPreferredSize(new Dimension(0, 32));
        if (field instanceof JTextField tf) {
            tf.setFont(new Font("Arial", Font.PLAIN, 13));
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BDR),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        } else if (field instanceof JComboBox<?> cb) {
            cb.setFont(new Font("Arial", Font.PLAIN, 13));
            cb.setBackground(Color.WHITE);
        } else if (field instanceof JSpinner sp) {
            sp.setFont(new Font("Arial", Font.PLAIN, 13));
        }

        g.gridx = 1; g.weightx = 0.68;
        g.insets = new Insets(4, 0, 4, 2);
        form.add(field, g);

        return row + 1;
    }

    private void loadStudentData() {
        studentIdField.setText(editingStudent.getStudentId());
        nameField.setText(editingStudent.getFullName());
        genderCombo.setSelectedItem(editingStudent.getGender());
        dobSpinner.setValue(editingStudent.getDateOfBirth());
        emailField.setText(editingStudent.getEmail());
        phoneField.setText(editingStudent.getPhoneNumber());
        addressField.setText(editingStudent.getAddress());
        courseCombo.setSelectedItem(editingStudent.getCourse());
        departmentCombo.setSelectedItem(editingStudent.getDepartment());
        semesterSpinner.setValue(editingStudent.getSemester());
        enrollmentSpinner.setValue(editingStudent.getEnrollmentDate());
    }

    private void saveStudent() {
        if (ValidationService.isEmpty(studentIdField.getText())) {
            err("Student ID is required!"); return;
        }
        if (!ValidationService.isValidStudentId(studentIdField.getText()) && editingStudent == null) {
            err("Student ID must be in format: STU001"); return;
        }
        if (ValidationService.isEmpty(nameField.getText())) {
            err("Full Name is required!"); return;
        }
        if (!ValidationService.isValidName(nameField.getText())) {
            err("Name should contain only letters!"); return;
        }
        if (ValidationService.isEmpty(emailField.getText())) {
            err("Email is required!"); return;
        }
        if (!ValidationService.isValidEmail(emailField.getText())) {
            err("Invalid email format!"); return;
        }
        if (ValidationService.isEmpty(phoneField.getText())) {
            err("Phone number is required!"); return;
        }
        if (!ValidationService.isValidPhoneNumber(phoneField.getText())) {
            err("Phone number must be 10 digits starting with 6–9!"); return;
        }

        Student s = new Student();
        s.setStudentId(studentIdField.getText().trim());
        s.setFullName(nameField.getText().trim());
        s.setGender((String) genderCombo.getSelectedItem());
        s.setDateOfBirth((Date) dobSpinner.getValue());
        s.setEmail(emailField.getText().trim());
        s.setPhoneNumber(phoneField.getText().trim());
        s.setAddress(addressField.getText().trim());
        s.setCourse((String) courseCombo.getSelectedItem());
        s.setDepartment((String) departmentCombo.getSelectedItem());
        s.setSemester((int) semesterSpinner.getValue());
        s.setEnrollmentDate((Date) enrollmentSpinner.getValue());

        boolean ok;
        if (editingStudent == null) {
            ok = studentService.addStudent(s);
            if (ok) info("Student added successfully!");
            else  { err("Failed to add student! ID may already exist."); return; }
        } else {
            ok = studentService.updateStudent(s);
            if (ok) info("Student updated successfully!");
            else  { err("Failed to update student!"); return; }
        }
        dispose();
    }

    private void err(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

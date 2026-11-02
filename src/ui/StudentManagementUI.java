package ui;

import service.StudentService;
import model.Student;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Main student records management frame displaying all students in a sortable,
 * searchable JTable, and providing actions for registration, editing, deletion, and CSV export.
 */
public class StudentManagementUI extends JFrame {

    private static final Color PRIMARY    = new Color(41, 128, 185);
    private static final Color PAGE_BG    = new Color(240, 242, 245);
    private static final Color ROW_ODD    = Color.WHITE;
    private static final Color ROW_EVEN   = new Color(245, 248, 252);
    private static final Color ROW_SELECT = new Color(187, 222, 251);

    private StudentService    studentService;
    private JTable            studentTable;
    private DefaultTableModel tableModel;
    private JTextField        searchField;

    public StudentManagementUI() {
        studentService = new StudentService();
        initComponents();
        loadStudentData();
    }

    private void initComponents() {
        setTitle("Student Management — Records");
        setSize(1300, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 580));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(PAGE_BG);

        root.add(createPageHeader(),   BorderLayout.NORTH);
        root.add(createContentArea(),  BorderLayout.CENTER);
        root.add(createButtonBar(),    BorderLayout.SOUTH);

        add(root);
    }

    private JPanel createPageHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        bar.setPreferredSize(new Dimension(0, 56));

        JLabel title = new JLabel("Student Records");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        bar.add(title, BorderLayout.WEST);
        bar.add(createControlPanel(), BorderLayout.EAST);
        return bar;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);

        JLabel searchIcon = new JLabel("⌕");
        searchIcon.setFont(new Font("Arial", Font.BOLD, 18));
        searchIcon.setForeground(Color.WHITE);

        searchField = new JTextField(18);
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(0, 32));
        searchField.putClientProperty("JTextField.placeholderText", "Search by ID, name or email…");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(160, 195, 230)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchStudents(); }
        });

        JButton refreshBtn = new JButton("⟳ Refresh");
        styleHeaderButton(refreshBtn);
        refreshBtn.addActionListener(e -> loadStudentData());

        JLabel sortLbl = new JLabel("Sort:");
        sortLbl.setForeground(Color.WHITE);
        sortLbl.setFont(new Font("Arial", Font.PLAIN, 13));

        String[] opts = {"by Name", "by Course", "by Department"};
        JComboBox<String> sortCombo = new JComboBox<>(opts);
        sortCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        sortCombo.setPreferredSize(new Dimension(130, 30));
        sortCombo.addActionListener(e ->
            sortStudents("Sort " + sortCombo.getSelectedItem()));

        panel.add(searchIcon);
        panel.add(searchField);
        panel.add(refreshBtn);
        panel.add(sortLbl);
        panel.add(sortCombo);
        return panel;
    }

    private void styleHeaderButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        Color base = new Color(52, 152, 219);
        btn.setBackground(base);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(115, 32));
        ButtonStyler.addHover(btn, base);
    }

    private JPanel createContentArea() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(PAGE_BG);
        wrap.setBorder(BorderFactory.createEmptyBorder(12, 14, 6, 14));

        String[] cols = {"ID", "Full Name", "Gender", "Email", "Phone", "Course", "Department", "Semester"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        studentTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? ROW_ODD : ROW_EVEN);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        studentTable.setFont(new Font("Arial", Font.PLAIN, 13));
        studentTable.setRowHeight(32);
        studentTable.setShowGrid(false);
        studentTable.setIntercellSpacing(new Dimension(0, 0));
        studentTable.setSelectionBackground(ROW_SELECT);
        studentTable.setSelectionForeground(Color.BLACK);

        JTableHeader header = studentTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 13));
        header.setBackground(new Color(44, 62, 80));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        int[] widths = {80, 160, 80, 180, 110, 140, 150, 70};
        for (int i = 0; i < widths.length; i++)
            studentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(studentTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));
        scroll.getViewport().setBackground(Color.WHITE);

        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel createButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 12));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        Dimension btnSize = new Dimension(155, 38);

        JButton addBtn    = new JButton("+ Add Student");
        JButton editBtn   = new JButton("✎ Edit Student");
        JButton deleteBtn = new JButton("✕ Delete Student");
        JButton exportBtn = new JButton("⤓ Export to CSV");
        JButton backBtn   = new JButton("← Dashboard");

        ButtonStyler.styleButton(addBtn,    new Color(39, 174,  96), btnSize);
        ButtonStyler.styleButton(editBtn,   new Color(52, 152, 219), btnSize);
        ButtonStyler.styleButton(deleteBtn, new Color(192, 57,  43), btnSize);
        ButtonStyler.styleButton(exportBtn, new Color(142, 68, 173), btnSize);
        ButtonStyler.styleButton(backBtn,   new Color(127, 140, 141), btnSize);

        addBtn.addActionListener(e    -> addStudent());
        editBtn.addActionListener(e   -> editStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        exportBtn.addActionListener(e -> exportToCSV());
        backBtn.addActionListener(e   -> { new DashboardUI().setVisible(true); dispose(); });

        bar.add(addBtn);
        bar.add(editBtn);
        bar.add(deleteBtn);
        bar.add(exportBtn);
        bar.add(backBtn);
        return bar;
    }

    private void loadStudentData() {
        tableModel.setRowCount(0);
        for (Student s : studentService.getAllStudents()) addRow(s);
    }

    private void searchStudents() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) { loadStudentData(); return; }

        tableModel.setRowCount(0);
        List<Student> results = studentService.searchStudents(term);
        for (Student s : results) addRow(s);

        if (results.isEmpty())
            JOptionPane.showMessageDialog(this, "No students found!", "Search Result",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void sortStudents(String sortBy) {
        List<Student> students = studentService.getAllStudents();
        if      (sortBy.contains("Name"))       students.sort((a, b) -> a.getFullName().compareTo(b.getFullName()));
        else if (sortBy.contains("Course"))     students.sort((a, b) -> a.getCourse().compareTo(b.getCourse()));
        else if (sortBy.contains("Department")) students.sort((a, b) -> a.getDepartment().compareTo(b.getDepartment()));

        tableModel.setRowCount(0);
        for (Student s : students) addRow(s);
    }

    private void addRow(Student s) {
        tableModel.addRow(new Object[]{
            s.getStudentId(), s.getFullName(), s.getGender(),
            s.getEmail(), s.getPhoneNumber(), s.getCourse(),
            s.getDepartment(), s.getSemester()
        });
    }

    private void addStudent() {
        // Modal JDialog setVisible(true) blocks the UI thread until closed,
        // ensuring the table refreshes immediately with the latest database records.
        new AddEditStudentUI(this, null).setVisible(true);
        loadStudentData();
    }

    private void editStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to edit!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) tableModel.getValueAt(row, 0);
        Student student = studentService.getStudentById(id);
        if (student == null) {
            JOptionPane.showMessageDialog(this,
                "Could not load student data for ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new AddEditStudentUI(this, student).setVisible(true);
        loadStudentData();
    }

    private void deleteStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to delete!", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            String id = (String) tableModel.getValueAt(row, 0);
            if (studentService.deleteStudent(id)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudentData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete student! Check console for details.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToCSV() {
        String filename = "students_export_" + System.currentTimeMillis() + ".csv";
        try (java.io.FileWriter fw = new java.io.FileWriter(filename)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < studentTable.getColumnCount(); i++) {
                sb.append(studentTable.getColumnName(i));
                if (i < studentTable.getColumnCount() - 1) sb.append(",");
            }
            sb.append("\n");
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    sb.append(tableModel.getValueAt(i, j));
                    if (j < tableModel.getColumnCount() - 1) sb.append(",");
                }
                sb.append("\n");
            }
            fw.write(sb.toString());
            JOptionPane.showMessageDialog(this, "Exported successfully to: " + filename);
        } catch (java.io.IOException e) {
            System.err.println("[EXPORT ERROR] " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Export failed:\n" + e.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

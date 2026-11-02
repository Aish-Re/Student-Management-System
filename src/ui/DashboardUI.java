package ui;

import service.StudentService;
import model.Student;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Main application dashboard displaying real-time enrollment statistics,
 * recent registrations, and sidebar navigation controls.
 */
public class DashboardUI extends JFrame {

    private static final Color HEADER_BG  = new Color(41, 128, 185);
    private static final Color SIDEBAR_BG = new Color(44, 62, 80);
    private static final Color SIDEBAR_HV = new Color(55, 76, 98);
    private static final Color ACCENT_BLU = new Color(52, 152, 219);
    private static final Color PAGE_BG    = new Color(240, 242, 245);
    private static final Color CARD_WHITE = Color.WHITE;

    private StudentService studentService;
    private JLabel         totalStudentsLabel;
    private JLabel         coursesCountLabel;
    private JLabel         deptCountLabel;
    private JPanel         recentStudentsPanel;

    public DashboardUI() {
        studentService = new StudentService();
        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        setTitle("Student Management System — Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.add(createHeader(),  BorderLayout.NORTH);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createCenter(),  BorderLayout.CENTER);
        add(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        header.setPreferredSize(new Dimension(0, 64));

        JLabel appLabel = new JLabel("Student Management System");
        appLabel.setFont(new Font("Arial", Font.BOLD, 22));
        appLabel.setForeground(Color.WHITE);

        String today = new SimpleDateFormat("EEE, dd MMM yyyy  |  HH:mm").format(new Date());
        JLabel infoLabel = new JLabel("Welcome, Admin   \u2022   " + today);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(210, 230, 250));

        header.add(appLabel,  BorderLayout.WEST);
        header.add(infoLabel, BorderLayout.EAST);
        return header;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("NAVIGATION");
        brand.setFont(new Font("Arial", Font.BOLD, 11));
        brand.setForeground(new Color(150, 170, 190));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(brand);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] labels = {"Dashboard", "Student Management", "Reports", "Logout"};
        String[] iconTypes = {"dashboard", "students", "reports", "logout"};

        for (int i = 0; i < labels.length; i++) {
            Icon icon = new VectorSidebarIcon(iconTypes[i], new Color(189, 195, 199));
            JButton btn = makeSidebarButton(labels[i], icon);

            if (i == labels.length - 1) {
                sidebar.add(Box.createVerticalGlue());
                JSeparator sep = new JSeparator();
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                sep.setForeground(new Color(55, 75, 95));
                sidebar.add(sep);
                sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
            }

            final int idx = i;
            btn.addActionListener(e -> handleMenuClick(idx));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 4)));
        }
        sidebar.add(Box.createRigidArea(new Dimension(0, 16)));
        return sidebar;
    }

    private JButton makeSidebarButton(String text, Icon icon) {
        JButton btn = new JButton(text, icon);
        btn.setIconTextGap(14);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(230, 44));
        btn.setMinimumSize(new Dimension(230, 44));
        btn.setPreferredSize(new Dimension(230, 44));
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(new Color(220, 230, 240));
        btn.setFont(new Font("Arial", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        Border normalBorder = BorderFactory.createCompoundBorder(
            new MatteBorder(0, 4, 0, 0, SIDEBAR_BG),
            BorderFactory.createEmptyBorder(0, 16, 0, 0)
        );
        Border hoverBorder = BorderFactory.createCompoundBorder(
            new MatteBorder(0, 4, 0, 0, ACCENT_BLU),
            BorderFactory.createEmptyBorder(0, 16, 0, 0)
        );

        btn.setBorder(normalBorder);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(SIDEBAR_HV);
                btn.setBorder(hoverBorder);
                btn.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
                btn.setBorder(normalBorder);
                btn.setForeground(new Color(220, 230, 240));
            }
        });
        return btn;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        center.setBackground(PAGE_BG);

        center.add(createStatRow(),         BorderLayout.NORTH);
        center.add(createRecentSection(),   BorderLayout.CENTER);
        return center;
    }

    private JPanel createStatRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setBackground(PAGE_BG);
        row.setPreferredSize(new Dimension(0, 110));

        totalStudentsLabel = new JLabel("0", SwingConstants.CENTER);
        row.add(createStatCard("Total Students",  totalStudentsLabel,  new Color(39, 174,  96), "students"));

        coursesCountLabel  = new JLabel("0", SwingConstants.CENTER);
        row.add(createStatCard("Active Courses",  coursesCountLabel,   new Color(52, 152, 219), "courses"));

        deptCountLabel     = new JLabel("0", SwingConstants.CENTER);
        row.add(createStatCard("Departments",     deptCountLabel,      new Color(142, 68, 173), "departments"));

        return row;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent, String iconType) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(CARD_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 5, 0, 0, accent),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel iconLbl = new JLabel(new VectorStatIcon(iconType, accent));
        iconLbl.setVerticalAlignment(SwingConstants.CENTER);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setBackground(CARD_WHITE);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 30));
        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLbl.setForeground(new Color(120, 120, 120));

        text.add(valueLabel);
        text.add(titleLbl);

        card.add(iconLbl, BorderLayout.WEST);
        card.add(text,    BorderLayout.CENTER);
        return card;
    }

    private JPanel createRecentSection() {
        JLabel heading = new JLabel("  Recent Student Registrations");
        heading.setFont(new Font("Arial", Font.BOLD, 13));
        heading.setForeground(Color.WHITE);
        heading.setOpaque(true);
        heading.setBackground(HEADER_BG);
        heading.setPreferredSize(new Dimension(0, 36));

        recentStudentsPanel = new JPanel();
        recentStudentsPanel.setLayout(new BoxLayout(recentStudentsPanel, BoxLayout.Y_AXIS));
        recentStudentsPanel.setBackground(CARD_WHITE);

        JScrollPane scroll = new JScrollPane(recentStudentsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 220)));
        scroll.getViewport().setBackground(CARD_WHITE);

        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(PAGE_BG);
        section.add(heading, BorderLayout.NORTH);
        section.add(scroll,  BorderLayout.CENTER);
        return section;
    }

    private void loadDashboardData() {
        int totalStudents = studentService.getTotalStudentsCount();
        int activeCourses = studentService.getActiveCoursesCount();
        int departments   = studentService.getDepartmentsCount();

        totalStudentsLabel.setText(String.valueOf(totalStudents));
        coursesCountLabel.setText(String.valueOf(activeCourses));
        deptCountLabel.setText(String.valueOf(departments));

        loadRecentStudents();
    }

    private void loadRecentStudents() {
        recentStudentsPanel.removeAll();
        List<Student> recent = studentService.getRecentStudents();

        if (recent.isEmpty()) {
            JLabel empty = new JLabel("No students registered yet.", SwingConstants.CENTER);
            empty.setFont(new Font("Arial", Font.ITALIC, 13));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            recentStudentsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            recentStudentsPanel.add(empty);
        } else {
            boolean shade = false;
            for (Student s : recent) {
                recentStudentsPanel.add(createStudentRow(s, shade));
                shade = !shade;
            }
        }

        recentStudentsPanel.revalidate();
        recentStudentsPanel.repaint();
    }

    private JPanel createStudentRow(Student s, boolean shaded) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(shaded ? new Color(248, 250, 252) : CARD_WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(230, 234, 238)),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        String initial = (s.getFullName() != null && !s.getFullName().trim().isEmpty())
            ? s.getFullName().trim().substring(0, 1).toUpperCase()
            : "S";
        JLabel avatar = new JLabel(new AvatarIcon(initial, new Color(225, 238, 248), new Color(41, 128, 185)));

        JLabel name   = new JLabel(s.getFullName() + "  (" + s.getStudentId() + ")");
        name.setFont(new Font("Arial", Font.BOLD, 13));

        JLabel detail = new JLabel(s.getCourse() + "  \u2014  Semester " + s.getSemester());
        detail.setFont(new Font("Arial", Font.PLAIN, 11));
        detail.setForeground(new Color(120, 120, 120));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        info.add(name);
        info.add(detail);

        row.add(avatar, BorderLayout.WEST);
        row.add(info,   BorderLayout.CENTER);
        return row;
    }

    private void handleMenuClick(int idx) {
        switch (idx) {
            case 0:
                loadDashboardData();
                break;
            case 1:
                new StudentManagementUI().setVisible(true);
                dispose();
                break;
            case 2:
                generateReport();
                break;
            case 3:
                int ok = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    new LoginUI().setVisible(true);
                    dispose();
                }
                break;
        }
    }

    private void generateReport() {
        List<Student> students = studentService.getAllStudents();
        StringBuilder sb = new StringBuilder();
        sb.append("STUDENT MANAGEMENT SYSTEM REPORT\n");
        sb.append("Generated: ").append(new SimpleDateFormat("dd MMM yyyy HH:mm").format(new Date())).append("\n");
        sb.append("=".repeat(50)).append("\n\n");
        sb.append("Total Students: ").append(students.size()).append("\n\n");
        sb.append("Student List:\n");
        sb.append("-".repeat(50)).append("\n");
        for (Student s : students) {
            sb.append(String.format("ID: %s | Name: %s | Course: %s | Semester: %d%n",
                s.getStudentId(), s.getFullName(), s.getCourse(), s.getSemester()));
        }

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(ta);
        sp.setPreferredSize(new Dimension(620, 420));
        JOptionPane.showMessageDialog(this, sp, "Student Report", JOptionPane.INFORMATION_MESSAGE);
    }

    /** 18x18 vector icons for sidebar navigation items */
    private static class VectorSidebarIcon implements Icon {
        private final String type;
        private final Color color;

        public VectorSidebarIcon(String type, Color color) {
            this.type = type;
            this.color = color;
        }

        @Override public int getIconWidth() { return 18; }
        @Override public int getIconHeight() { return 18; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);

            switch (type) {
                case "dashboard":
                    g2.fillRoundRect(x + 1, y + 1, 6, 6, 2, 2);
                    g2.fillRoundRect(x + 9, y + 1, 6, 6, 2, 2);
                    g2.fillRoundRect(x + 1, y + 9, 6, 6, 2, 2);
                    g2.fillRoundRect(x + 9, y + 9, 6, 6, 2, 2);
                    break;
                case "students":
                    g2.fillOval(x + 6, y + 1, 6, 6);
                    g2.fillRoundRect(x + 2, y + 8, 14, 7, 4, 4);
                    break;
                case "reports":
                    g2.fillRoundRect(x + 2, y + 9, 3, 7, 2, 2);
                    g2.fillRoundRect(x + 7, y + 5, 3, 11, 2, 2);
                    g2.fillRoundRect(x + 12, y + 1, 3, 15, 2, 2);
                    break;
                case "logout":
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(x + 2, y + 2, 7, 13, 2, 2);
                    g2.drawLine(x + 7, y + 9, x + 15, y + 9);
                    g2.drawLine(x + 12, y + 6, x + 15, y + 9);
                    g2.drawLine(x + 12, y + 12, x + 15, y + 9);
                    break;
            }
            g2.dispose();
        }
    }

    /** 38x38 vector icons for dashboard statistic cards */
    private static class VectorStatIcon implements Icon {
        private final String type;
        private final Color accent;

        public VectorStatIcon(String type, Color accent) {
            this.type = type;
            this.accent = accent;
        }

        @Override public int getIconWidth() { return 38; }
        @Override public int getIconHeight() { return 38; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color badgeBg = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 26);
            g2.setColor(badgeBg);
            g2.fillOval(x, y, 38, 38);

            g2.setColor(accent);
            switch (type) {
                case "students":
                    g2.fillOval(x + 14, y + 8, 10, 10);
                    g2.fillRoundRect(x + 8, y + 20, 22, 10, 6, 6);
                    break;
                case "courses":
                    g2.fillRoundRect(x + 9, y + 10, 20, 5, 2, 2);
                    g2.fillRoundRect(x + 9, y + 17, 20, 5, 2, 2);
                    g2.fillRoundRect(x + 9, y + 24, 20, 5, 2, 2);
                    break;
                case "departments":
                    int[] xPoints = {x + 19, x + 8, x + 30};
                    int[] yPoints = {y + 9, y + 16, y + 16};
                    g2.fillPolygon(xPoints, yPoints, 3);
                    g2.fillRect(x + 10, y + 18, 4, 10);
                    g2.fillRect(x + 17, y + 18, 4, 10);
                    g2.fillRect(x + 24, y + 18, 4, 10);
                    g2.fillRect(x + 8, y + 28, 22, 3);
                    break;
            }
            g2.dispose();
        }
    }

    /** 30x30 circular avatar icon with student initial */
    private static class AvatarIcon implements Icon {
        private final String initial;
        private final Color bg;
        private final Color fg;

        public AvatarIcon(String initial, Color bg, Color fg) {
            this.initial = initial;
            this.bg = bg;
            this.fg = fg;
        }

        @Override public int getIconWidth() { return 30; }
        @Override public int getIconHeight() { return 30; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(bg);
            g2.fillOval(x, y, 30, 30);

            g2.setColor(fg);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int strW = fm.stringWidth(initial);
            int strH = fm.getAscent();

            g2.drawString(initial, x + (30 - strW) / 2, y + (30 + strH) / 2 - 2);
            g2.dispose();
        }
    }
}

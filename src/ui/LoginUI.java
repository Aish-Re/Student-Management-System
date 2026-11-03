package ui;

import service.AuthService;
import database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LoginUI extends JFrame {

    private static final Color PRIMARY   = new Color(41, 128, 185);
    private static final Color SUCCESS   = new Color(39, 174,  96);
    private static final Color BG_TOP    = new Color(41, 128, 185);
    private static final Color BG_BOT    = new Color(84, 173, 210);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private AuthService    authService;

    public LoginUI() {
        authService = new AuthService();
        initComponents();
        checkDatabaseConnection();
    }

    private void checkDatabaseConnection() {
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(this,
                "Failed to connect to database!\nPlease check MySQL connection.",
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setTitle("Student Management System — Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setPaint(new GradientPaint(0, 0, BG_TOP, 0, getHeight(), BG_BOT));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(32, 40, 28, 40));
        card.setPreferredSize(new Dimension(380, 440));

        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;
        g.gridx   = 0;
        g.gridwidth = 1;

        JLabel iconLabel = new JLabel(new AcademicCapIcon(PRIMARY));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy  = 0;
        g.insets = new Insets(0, 0, 8, 0);
        card.add(iconLabel, g);

        JLabel title = new JLabel("Student Management System", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 19));
        title.setForeground(PRIMARY);
        g.gridy  = 1;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(title, g);

        JLabel subtitle = new JLabel("Administrator Login", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitle.setForeground(new Color(130, 130, 130));
        g.gridy  = 2;
        g.insets = new Insets(0, 0, 22, 0);
        card.add(subtitle, g);

        g.gridy  = 3;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(makeFieldLabel("Username"), g);

        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(0, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        g.gridy  = 4;
        g.insets = new Insets(0, 0, 14, 0);
        card.add(usernameField, g);

        g.gridy  = 5;
        g.insets = new Insets(0, 0, 4, 0);
        card.add(makeFieldLabel("Password"), g);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(0, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        g.gridy  = 6;
        g.insets = new Insets(0, 0, 22, 0);
        card.add(passwordField, g);

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(SUCCESS);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setOpaque(true);
        loginBtn.setPreferredSize(new Dimension(0, 44));
        ButtonStyler.addHover(loginBtn, SUCCESS);
        loginBtn.addActionListener(new LoginAction());

        g.gridy  = 7;
        g.insets = new Insets(0, 0, 14, 0);
        card.add(loginBtn, g);

        JLabel hint = new JLabel("Default: admin / admin123", SwingConstants.CENTER);
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setForeground(new Color(170, 170, 170));
        g.gridy  = 8;
        g.insets = new Insets(0, 0, 0, 0);
        card.add(hint, g);

        String today = new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date());
        JLabel footer = new JLabel(today, SwingConstants.CENTER);
        footer.setFont(new Font("Arial", Font.PLAIN, 11));
        footer.setForeground(new Color(255, 255, 255, 180));

        JPanel wrap = new JPanel(new BorderLayout(0, 12));
        wrap.setOpaque(false);
        wrap.add(card,   BorderLayout.CENTER);
        wrap.add(footer, BorderLayout.SOUTH);

        mainPanel.add(wrap);
        add(mainPanel);

        getRootPane().setDefaultButton(loginBtn);
    }

    private JLabel makeFieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(new Color(60, 60, 60));
        return l;
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(LoginUI.this,
                    "Please enter both username and password!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!DatabaseConnection.testConnection()) {
                JOptionPane.showMessageDialog(LoginUI.this,
                    "Cannot connect to the MySQL database!\nPlease verify that MySQL Server is running.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authService.login(username, password)) {
                JOptionPane.showMessageDialog(LoginUI.this,
                    "Login Successful! Welcome " + username,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                new DashboardUI().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginUI.this,
                    "Invalid Username or Password!",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }

    private static class AcademicCapIcon implements Icon {
        private final Color color;

        public AcademicCapIcon(Color color) {
            this.color = color;
        }

        @Override public int getIconWidth() { return 52; }
        @Override public int getIconHeight() { return 42; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            int[] xPoints = {x + 26, x + 48, x + 26, x + 4};
            int[] yPoints = {y + 8,  y + 18, y + 28, y + 18};
            g2.fillPolygon(xPoints, yPoints, 4);

            g2.fillRoundRect(x + 15, y + 23, 22, 10, 4, 4);

            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x + 48, y + 18, x + 48, y + 32);
            g2.fillOval(x + 46, y + 31, 5, 5);

            g2.dispose();
        }
    }
}

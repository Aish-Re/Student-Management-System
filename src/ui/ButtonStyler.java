package ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonStyler {

    public static void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        addHover(button, bgColor);
    }

    public static void styleButton(JButton button, Color bgColor, Dimension size) {
        styleButton(button, bgColor);
        button.setPreferredSize(size);
    }

    public static void styleDangerButton(JButton button) {
        Color red = new Color(231, 76, 60);
        styleButton(button, red);
    }


    public static void styleSuccessButton(JButton button) {
        Color green = new Color(39, 174, 96);
        styleButton(button, green);
    }

    public static Color getSubtleHover(Color base) {
        int r = Math.max(0, (int) (base.getRed() * 0.90));
        int g = Math.max(0, (int) (base.getGreen() * 0.90));
        int b = Math.max(0, (int) (base.getBlue() * 0.90));
        return new Color(r, g, b);
    }
    
    public static void addHover(JButton button, Color base) {
        Color hover = getSubtleHover(base);
        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            @Override public void mouseExited(MouseEvent e)  { button.setBackground(base); }
        });
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cms.util;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class UIUtils {

    // palette
    public static final Color VINTAGE_BG       = new Color(0xF5, 0xF0, 0xE6); // Aged Parchment
    public static final Color VINTAGE_PANEL    = new Color(0x2C, 0x24, 0x1E); // Dark Espresso
    public static final Color VINTAGE_GOLD     = new Color(0xC5, 0xA0, 0x4A); // Antique Gold
    public static final Color VINTAGE_CREAM    = new Color(0xE8, 0xD5, 0xC4); // Warm Cream
    public static final Color VINTAGE_ROSE     = new Color(0xA0, 0x4A, 0x4A); // Muted Rose (errors)
    public static final Color VINTAGE_SHADOW   = new Color(0x1A, 0x15, 0x12); // Deep Shadow

    // getters
    public static Color getBgColor() { return VINTAGE_BG; }
    public static Color getPanelColor() { return VINTAGE_PANEL; }
    public static Color getTextColor() { return VINTAGE_CREAM; }
    public static Color getDimTextColor() { return VINTAGE_CREAM.darker(); }
    public static Color getAccentColor() { return VINTAGE_GOLD; }

    // rounded border just for better aesthetic
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }
    
        
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
        
        

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }


    public static void styleMenuButton(JButton button) {
        button.setBackground(VINTAGE_PANEL);
        button.setForeground(VINTAGE_CREAM.darker());
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.getForeground().equals(VINTAGE_GOLD)) {
                    button.setForeground(VINTAGE_CREAM);
                    button.setBackground(VINTAGE_SHADOW);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getForeground().equals(VINTAGE_GOLD)) {
                    button.setForeground(VINTAGE_CREAM.darker());
                    button.setBackground(VINTAGE_PANEL);
                }
            }
        });
    }

    public static void styleActionButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(VINTAGE_PANEL);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 25, 10, 25));
        button.setOpaque(true);
    }

    public static void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(VINTAGE_CREAM);
        field.setBackground(VINTAGE_SHADOW);
        field.setCaretColor(VINTAGE_GOLD);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VINTAGE_GOLD.darker(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setSelectionColor(VINTAGE_GOLD);
        field.setSelectedTextColor(VINTAGE_PANEL);
        field.setOpaque(true);
    }

    public static void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setForeground(VINTAGE_CREAM);
        field.setBackground(VINTAGE_SHADOW);
        field.setCaretColor(VINTAGE_GOLD);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VINTAGE_GOLD.darker(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setSelectionColor(VINTAGE_GOLD);
        field.setSelectedTextColor(VINTAGE_PANEL);
        field.setOpaque(true);
    }
    

    public static class AvatarLabel extends JLabel {
        private Image image;
        private String initials;

        public AvatarLabel(String userId, String fullName, int size) {
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            
            initials = "";
            String[] parts = fullName.split(" ");
            if (parts.length > 0 && !parts[0].isEmpty()) initials += parts[0].charAt(0);
            if (parts.length > 1 && !parts[parts.length - 1].isEmpty()) initials += parts[parts.length - 1].charAt(0);
            
            try {
                java.io.File imgFilePng = new java.io.File("data/images/" + userId + ".png");
                java.io.File imgFileJpg = new java.io.File("data/images/" + userId + ".jpg");
                
                if (imgFilePng.exists()) {
                    image = new ImageIcon(imgFilePng.getPath()).getImage();
                } else if (imgFileJpg.exists()) {
                    image = new ImageIcon(imgFileJpg.getPath()).getImage();
                } else {
                    System.out.println("DEBUG: Image not found at " + imgFilePng.getAbsolutePath());
                }
            } catch (Exception e) {
                image = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int diameter = Math.min(getWidth(), getHeight()) - 2; 
            int x = 1; 
            int y = 1;

            if (image != null) {
                g2.setClip(new java.awt.geom.Ellipse2D.Double(x, y, diameter, diameter));
                g2.drawImage(image, x, y, diameter, diameter, this);
                
                g2.setClip(null); 
                g2.setColor(VINTAGE_GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x, y, diameter, diameter);
            } else {

                g2.setColor(VINTAGE_SHADOW);
                g2.fillOval(x, y, diameter, diameter);
                
                g2.setColor(VINTAGE_GOLD);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(x, y, diameter, diameter);
                
                g2.setColor(VINTAGE_GOLD);
                g2.setFont(new Font("Georgia", Font.BOLD, diameter / 2));
                FontMetrics fm = g2.getFontMetrics();
                int textX = x + (diameter - fm.stringWidth(initials)) / 2;
                int textY = y + ((diameter - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(initials, textX, textY);
            }
            g2.dispose();
        }
    }
}
    
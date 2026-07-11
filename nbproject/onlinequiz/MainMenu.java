package onlinequiz;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Online Quiz System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 10, 10, 10);

        c.gridx = 0;
        c.gridy = 0;
        add(loginBtn, c);

        c.gridx = 0;
        c.gridy = 1;
        add(registerBtn, c);

        // Open login page
        loginBtn.addActionListener(e ->
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true)));

        // Shortcut: directly go to registration
        registerBtn.addActionListener(e ->
                SwingUtilities.invokeLater(() -> {
                    LoginFrame reg = new LoginFrame();
                    reg.setVisible(true);
                    JOptionPane.showMessageDialog(reg,
                            "👉 Enter a new username & password, then click Register.");
                }));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainMenu().setVisible(true));
    }
}

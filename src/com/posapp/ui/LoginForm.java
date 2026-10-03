package com.posapp.ui;

import com.posapp.dao.EmployeeDAO;
import com.posapp.model.Employee;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JPanel mainPanel;
    private JPanel panelLeft;
    private JPanel panelRight;
    private JPanel panelTop;
    private JPanel panelBottom;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public LoginForm() {
        setTitle("Clothing Store POS System - Login");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(810,510);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);

        // Login button click
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });

        // Cancel button click
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Press Enter in password field = same as clicking Login
        txtPassword.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Ask EmployeeDAO to check username + password against the database
        Employee employee = employeeDAO.login(username, password);

        if (employee != null) {
            // Login success -> open Dashbord
            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, " + employee.getFullName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
            // TODO: uncomment this line after DashboardForm is built
            // new DashboardForm(employee).setVisible(true);
            new DashboardForm(employee).setVisible(true);

            this.dispose();
        } else {
            // Login failed -> show error, Clear password field
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.\nPlease try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            txtUsername.requestFocus();
            txtPassword.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm form = new LoginForm();
            form.setVisible(true);
        });
    }
}

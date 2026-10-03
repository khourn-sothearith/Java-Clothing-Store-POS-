package com.posapp;

import com.posapp.ui.LoginForm;

import javax.swing.*;

/**
 * Entry point for the Clothing Store POS System.
 * Run this class to start the application.
 */
public class Main {
    public static void main(String[] args) {
        // Run on the Swing Event Dispatch Thread (EDT) - best practice for Swing apps
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
//Admin account:
//
//Field	Value
//Username	admin
//Password	admin123
//
//Cashier account:
//
//Field	Value
//Username	cashier1
//Password	cashier123

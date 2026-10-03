package com.posapp.dao;

import com.posapp.db.DBConnection;
import com.posapp.model.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Handles all database operations for the 'employee' table.
 * Used mainly for login authentication.
 */
public class EmployeeDAO {
    /**
     * Checks username + password against the database.
     * Returns the matching Employee object if valid, or null if invalid.
     */
    public Employee login(String username, String password) {
        String sql = "select * from employee where username=? and password=?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Employee emp = new Employee();
                    emp.setEmployeeId(rs.getInt("employee_id"));
                    emp.setUsername(rs.getString("username"));
                    emp.setPassword(rs.getString("password"));
                    emp.setFullName(rs.getString("full_name"));
                    emp.setRole(rs.getString("role"));
                    return emp;
                }
            }
        } catch (SQLException e) {
            System.out.println("Login query failed" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
        EmployeeDAO dao = new EmployeeDAO();
        Employee emp = dao.login("admin", "admin123");
        if (emp != null) {
            System.out.println("Login success: " + emp.getFullName() + " - " + emp.getRole());
        } else {
            System.out.println("Login failed - no match found");
        }
    }
}

package com.posapp.dao;

import com.posapp.db.DBConnection;
import com.posapp.model.Customer;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the 'customer' table.
 */
public class CustomerDAO {
    /**
     * Returns every customer.
     */
    public List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql =  "SELECT * FROM customer ORDER BY customer_id";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getAllCustomers failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

//    /**
//     * Searches customers by name (partial match).
//     */
//    public List<Customer> searchCustomer(String keyword) {
//        List<Customer> list = new ArrayList<>();
//        String sql = "SELECT * FROM customer WHERE name LIKE ? ORDER BY customer_id";
//        Connection conn = DBConnection.getConnection();
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, "%" + keyword + "%");
//            try (ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    list.add(mapRow(rs));
//                }
//            }
//        } catch (SQLException e) {
//            System.out.println("searchCustomer failed: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return list;
//    }
    /**
     * Searches customers by name OR phone (partial match).
     */
    public List<Customer> searchCustomer(String keyword) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customer WHERE name LIKE ? OR phone LIKE ? ORDER BY customer_id";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("searchCustomer failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns a single customer by id, or null if not found.
     */
    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customer WHERE customer_id = ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("getCustomerById failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new customer. Returns true if successful.
     */
    public boolean addCustomer(Customer c) {
        String sql = "INSERT INTO customer (name, phone, discount) VALUES (?, ?, ?)";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setInt(3, c.getDiscount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("addCustomer failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing customer (matched by customer_id). Returns true if successful.
     */
    public boolean updateCustomer(Customer c) {
        String sql = "UPDATE customer SET name=?, phone=?, discount=? WHERE customer_id=?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName());
            ps.setString(2, c.getPhone());
            ps.setInt(3, c.getDiscount());
            ps.setInt(4, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateCustomer failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a customer by id. Returns true if successful.
     */
    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customer_id=?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("deleteCustomer failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setName(rs.getString("name"));
        c.setPhone(rs.getString("phone"));
        c.setDiscount(rs.getInt("discount"));
        return c;
    }

    /**
     * Quick test method.
     */
    public static  void main(String[] args) {
        CustomerDAO dao = new CustomerDAO();
        List<Customer> customers = dao.getAllCustomers();
        System.out.println("Found " + customers.size() + " customers");

        for (Customer c : customers) {
            System.out.println(" - " + c.getCustomerId() + ": " + c.getName() + " | " + c.getPhone());
        }
    }
}

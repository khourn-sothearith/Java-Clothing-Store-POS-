package com.posapp.dao;

import com.posapp.db.DBConnection;
import com.posapp.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the 'category' table.
 */
public class CategoryDAO {
    /**
     * Returns every category in the database.
     * Used to fill the JComboBox in the Product form.
     */
    public List<Category> getAllCategories(){
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY category_name";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setCategoryName(rs.getString("category_name"));
                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println("getAllCategories failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Adds a new category. Returns true if successful.
     */
    public boolean addCategory(String categoryName){
        String sql = "INSERT INTO category (category_name) VALUES (?)";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("addCategory failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a category by id. Return true if successful.
     * Note: product referencing this category will have category will have category_id set to NULL
     * automatically (see ON DELETE SET NULL in the schema).
     */
    public boolean deleteCategory(int categoryId){
        String sql = "DELETE FROM category WHERE category_id = ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("deleteCategory failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Quick test method.
     */
    public static void main(String[] args) {
        CategoryDAO dao = new CategoryDAO();
        List<Category> categories = dao.getAllCategories();
        System.out.println("Found " + categories.size() + " categories");
        for (Category c : categories) {
            System.out.println(" - " + c.getCategoryId() + ": " + c.getCategoryName());
        }
    }
}

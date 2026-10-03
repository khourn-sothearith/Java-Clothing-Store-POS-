package com.posapp.dao;

import com.posapp.db.DBConnection;
import com.posapp.model.Product;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Handles all database operations for the 'product' table.
 */
public class ProductDAO {
    /**
     * Returns every product, joined with category_name for display in tables.
     */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name " +
                "FROM product p LEFT JOIN category c ON p.category_id = c.category_id " +
                "ORDER BY p.product_id";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getAllProducts failed: " + e.getMessage());
        }
        return list;
    }

    /**
     * Searches products by name (partial match), used by the Search box
     * in the Product form and the Sales POS screen.
     */
    public List<Product> searchProducts(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name " +
                "FROM product p LEFT JOIN category c ON p.category_id = c.category_id " +
                "WHERE p.product_name LIKE ? ORDER BY p.product_id";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("searchProducts failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    /**
     * Returns a single product by id, or null if not found.
     * Used when checking stock during checkout.
     */
    public Product getProductById(int productId) {
        String sql = "SELECT p.*, c.category_name " +
                "FROM product p LEFT JOIN category c ON p.category_id = c.category_id " +
                "WHERE p.product_id = ?";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("getProductById failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns products where quantity is below low_stock_threshold.
     * Used by the Inventory form's Low Stock Alert.
     */
    public List<Product> getLowStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name " +
                "FROM product p LEFT JOIN category c ON p.category_id = c.category_id " +
                "WHERE p.quantity < p.low_stock_threshold ORDER BY p.quantity ASC";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.out.println("getLowStockProducts failed: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Adds a new product. Returns true if successful.
     */
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO product (product_name, category_id, size, color, price, quantity, low_stock_threshold) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getSize());
            ps.setString(4, p.getColor());
            ps.setBigDecimal(5, p.getPrice());
            ps.setInt(6, p.getQuantity());
            ps.setInt(7, p.getLowStockThreshold());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("addProduct failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing product (matched by product_id). Returns true if successful.
     */
    public boolean updateProduct(Product p) {
        String sql = "UPDATE product SET product_name=?, category_id=?, size=?, color=?, " +
                "price=?, quantity=?, low_stock_threshold=? WHERE product_id=?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setString(3, p.getSize());
            ps.setString(4, p.getColor());
            ps.setBigDecimal(5, p.getPrice());
            ps.setInt(6, p.getQuantity());
            ps.setInt(7,p.getLowStockThreshold());
            ps.setInt(8, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("updateProduct failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a product by id. Returns true if successful.
     */
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id=?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("deleteProduct failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reduces stock quantity after a sale (called once per cart item during checkout).
     * Returns true if successful.
     */
    public boolean reduceStock(int productId, int quantitySold) {
        String sql = "UPDATE product SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantitySold);
            ps.setInt(2, productId);
            ps.setInt(3, quantitySold);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("reduceStock failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper to map one ResultSet row into a Product object.
     * Keeps the mapping logic in one place instead of repeating it in every method.
     */
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setSize(rs.getString("size"));
        p.setColor(rs.getString("color"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setQuantity(rs.getInt("quantity"));
        p.setLowStockThreshold(rs.getInt("low_stock_threshold"));
        return p;
    }

    /**
     * Quick test method.
     */
    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();
        List<Product> products = dao.getAllProducts();
        System.out.println("Found " + products.size() + " products:");
        for (Product p : products) {
            System.out.println(" - " + p.getProductId() + ": " + p.getProductName()
                    + " | " + p.getCategoryName() + " | $" + p.getPrice()
                    + " | Qty: " + p.getQuantity());
        }
    }
}

package com.posapp.dao;

import com.posapp.db.DBConnection;
import com.posapp.model.Sale;
import com.posapp.model.SaleDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the 'sale' and 'sale_detail' tables.
 * This is where checkout actually happens.
 */
public class SaleDAO {

    /**
     * Saves a complete sale: the invoice header (sale) + every line item (sale_detail)
     * + reduces stock for each product sold.
     */
    public int checkout(Sale sale, List<SaleDetail> items) {
        Connection conn = DBConnection.getConnection();
        int generatedSaleId = -1;

        String insertSaleSql = "INSERT INTO sale " +
                "(invoice_no, customer_id, employee_id, grand_total, amount_paid, change_due, payment_method) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String insertDetailSql = "INSERT INTO sale_detail (sale_id, product_id, quantity, price, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";

        String reduceStockSql = "UPDATE product SET quantity = quantity - ? WHERE product_id = ? AND quantity >= ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(insertSaleSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sale.getInvoiceNo());
                if (sale.getCustomerId() == null) {
                    ps.setNull(2, Types.INTEGER);
                } else {
                    ps.setInt(2, sale.getCustomerId());
                }
                ps.setInt(3, sale.getEmployeeId());
                ps.setBigDecimal(4, sale.getGrandTotal());
                ps.setBigDecimal(5, sale.getAmountPaid());
                ps.setBigDecimal(6, sale.getChangeDue());
                ps.setString(7, sale.getPaymentMethod());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        generatedSaleId = keys.getInt(1);
                    }
                }
            }

            if (generatedSaleId == -1) {
                throw new SQLException("Failed to generate sale_id");
            }

            for (SaleDetail item : items) {
                try (PreparedStatement ps = conn.prepareStatement(insertDetailSql)) {
                    ps.setInt(1, generatedSaleId);
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    ps.setBigDecimal(4, item.getPrice());
                    ps.setBigDecimal(5, item.getSubtotal());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(reduceStockSql)) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, item.getProductId());
                    ps.setInt(3, item.getQuantity());
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Not enough stock for product_id " + item.getProductId());
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Checkout failed, rolling back: " + e.getMessage());
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            generatedSaleId = -1;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return generatedSaleId;
    }

    /**
     * Generates the next invoice number.
     */
    public String generateInvoiceNo() {
        String sql = "SELECT MAX(sale_id) AS max_id FROM sale";
        Connection conn = DBConnection.getConnection();
        int baseStart = 10030;
        int nextNumber;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("max_id") != null) {
                nextNumber = baseStart + rs.getInt("max_id") + 1;
            } else {
                nextNumber = baseStart + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            nextNumber = baseStart + 1;
        }
        return "INV-" + nextNumber;
    }

    /**
     * Returns the most recent sales for the Dashboard table.
     */
    public List<Sale> getRecentSales(int limit) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, COALESCE(c.name, 'Walk-in Customer') AS customer_name " +
                "FROM sale s LEFT JOIN customer c ON s.customer_id = c.customer_id " +
                "ORDER BY s.sale_id DESC LIMIT ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns every sale_detail row for a given sale_id.
     */
    public List<SaleDetail> getSaleDetail(int saleId) {
        List<SaleDetail> list = new ArrayList<>();
        String sql = "SELECT sd.*, p.product_name " +
                "FROM sale_detail sd JOIN product p ON sd.product_id = p.product_id " +
                "WHERE sd.sale_id = ?";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleDetail d = new SaleDetail();
                    d.setDetailId(rs.getInt("detail_id"));
                    d.setSaleId(rs.getInt("sale_id"));
                    d.setProductId(rs.getInt("product_id"));
                    d.setProductName(rs.getString("product_name"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setPrice(rs.getBigDecimal("price"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns today's sales count and total revenue for the Dashboard cards.
     */
    public Object[] getTodaySummary() {
        String sql = "SELECT COUNT(*) AS cnt, COALESCE(SUM(grand_total), 0) AS total " +
                "FROM sale WHERE DATE(sale_date) = CURDATE()";
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Object[]{rs.getInt("cnt"), rs.getBigDecimal("total")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[]{0, java.math.BigDecimal.ZERO};
    }

    // ── NEW: Report methods ────────────────────────────────────────────

    /**
     * Returns all sales for a specific date.
     * If date is null or empty, returns today's sales.
     * date format: "yyyy-MM-dd" e.g. "2026-07-14"
     */
    public List<Sale> getSalesByDate(String date) {
        List<Sale> list = new ArrayList<>();
        String sql;
        if (date == null || date.isEmpty()) {
            sql = "SELECT s.*, COALESCE(c.name, 'Walk-in Customer') AS customer_name " +
                    "FROM sale s LEFT JOIN customer c ON s.customer_id = c.customer_id " +
                    "WHERE DATE(s.sale_date) = CURDATE() ORDER BY s.sale_id DESC";
        } else {
            sql = "SELECT s.*, COALESCE(c.name, 'Walk-in Customer') AS customer_name " +
                    "FROM sale s LEFT JOIN customer c ON s.customer_id = c.customer_id " +
                    "WHERE DATE(s.sale_date) = ? ORDER BY s.sale_id DESC";
        }
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (date != null && !date.isEmpty()) {
                ps.setString(1, date);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns all sales for a specific month.
     * If yearMonth is null or empty, returns this month's sales.
     * yearMonth format: "yyyy-MM" e.g. "2026-07"
     */
    public List<Sale> getSalesByMonth(String yearMonth) {
        List<Sale> list = new ArrayList<>();
        String sql;
        if (yearMonth == null || yearMonth.isEmpty()) {
            sql = "SELECT s.*, COALESCE(c.name, 'Walk-in Customer') AS customer_name " +
                    "FROM sale s LEFT JOIN customer c ON s.customer_id = c.customer_id " +
                    "WHERE DATE_FORMAT(s.sale_date, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m') " +
                    "ORDER BY s.sale_id DESC";
        } else {
            sql = "SELECT s.*, COALESCE(c.name, 'Walk-in Customer') AS customer_name " +
                    "FROM sale s LEFT JOIN customer c ON s.customer_id = c.customer_id " +
                    "WHERE DATE_FORMAT(s.sale_date, '%Y-%m') = ? ORDER BY s.sale_id DESC";
        }
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (yearMonth != null && !yearMonth.isEmpty()) {
                ps.setString(1, yearMonth);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Returns top N best-selling products by total quantity sold.
     * Each row: [productName, categoryName, totalQtySold, totalRevenue]
     */
    public List<Object[]> getTopSellingProducts(int limit) {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.product_name, COALESCE(c.category_name, 'Unknown') AS category, " +
                "SUM(sd.quantity) AS total_qty, SUM(sd.subtotal) AS total_revenue " +
                "FROM sale_detail sd " +
                "JOIN product p ON sd.product_id = p.product_id " +
                "LEFT JOIN category c ON p.category_id = c.category_id " +
                "GROUP BY p.product_name, c.category_name " +
                "ORDER BY total_qty DESC LIMIT ?";
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                            rs.getString("product_name"),
                            rs.getString("category"),
                            rs.getInt("total_qty"),
                            rs.getBigDecimal("total_revenue")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Private helper ────────────────────────────────────────────────
    private Sale mapRow(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setSaleId(rs.getInt("sale_id"));
        s.setInvoiceNo(rs.getString("invoice_no"));
        s.setSaleDate(rs.getTimestamp("sale_date"));
        int custId = rs.getInt("customer_id");
        s.setCustomerId(rs.wasNull() ? null : custId);
        s.setCustomerName(rs.getString("customer_name"));
        s.setEmployeeId(rs.getInt("employee_id"));
        s.setGrandTotal(rs.getBigDecimal("grand_total"));
        s.setAmountPaid(rs.getBigDecimal("amount_paid"));
        s.setChangeDue(rs.getBigDecimal("change_due"));
        s.setPaymentMethod(rs.getString("payment_method"));
        return s;
    }
}
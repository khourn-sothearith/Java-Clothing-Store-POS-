package com.posapp.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

import com.posapp.dao.CategoryDAO;
import com.posapp.dao.ProductDAO;
import com.posapp.model.Category;
import com.posapp.model.Product;

public class ProductForm extends JPanel {
    private JPanel mainPanel;
    private JPanel panelHeader;
    private JPanel panelFields;
    private JPanel panelSearch;
    private JPanel panelButtons;
    private JPanel panelTable;
    private JLabel lblTitle;
    private JComboBox cmbCategory;
    private JComboBox cmbSize;
    private JTextField txtProductName;
    private JTextField txtColor;
    private JTextField txtPrice;
    private JTextField txtQuantity;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JTable tblProducts;

    //_________________________
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    private int selectedProductId = -1; // track which row is selected

    public ProductForm() {
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        setupSizeComboBox();
        loadCategories();
        setupTable();
        loadAllProducts();
        setupButtons();
        setupTableClickListener();
    }

    // Fill cmbSize with fixed clothing sizes
    private void setupSizeComboBox() {
        cmbSize.removeAllItems();
        cmbSize.addItem("S");
        cmbSize.addItem("M");
        cmbSize.addItem("L");
        cmbSize.addItem("XL");
        cmbSize.addItem("XXL");
    }

    // Load categories from database into cmbCategory
    private void loadCategories () {
        cmbCategory.removeAllItems();
        List<Category> categories = categoryDAO.getAllCategories();
        for (Category c : categories) {
            cmbCategory.addItem(c);
        }
    }
    // Set up JTable columns and style
    private void setupTable() {
        String[] columns = {"ID", "Product Name", "Category", "Size", "Color",
                "Price ($)", "Qty"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        tblProducts.setModel(model);
        tblProducts.setDefaultEditor(Object.class, null); // read only

        // Style
        tblProducts.setRowHeight(28);
        tblProducts.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tblProducts.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tblProducts.getTableHeader().setBackground(new Color(26, 58, 107));
        tblProducts.getTableHeader().setForeground(Color.WHITE);
        tblProducts.setSelectionBackground(new Color(232, 240, 250));
        tblProducts.setGridColor(new Color(230, 230, 230));

        // Column widths
        tblProducts.getColumnModel().getColumn(0).setPreferredWidth(40);
        // ID
        tblProducts.getColumnModel().getColumn(1).setPreferredWidth(180);
        // Name
        tblProducts.getColumnModel().getColumn(2).setPreferredWidth(100);
        // Category
        tblProducts.getColumnModel().getColumn(3).setPreferredWidth(50);
        // Size
        tblProducts.getColumnModel().getColumn(4).setPreferredWidth(80);
        // Color
        tblProducts.getColumnModel().getColumn(5).setPreferredWidth(80);
        // Price
        tblProducts.getColumnModel().getColumn(6).setPreferredWidth(50);
        // Qty
    }
    // Load all products into the table
    private void loadAllProducts() {
        populateTable(productDAO.getAllProducts());
    }

    // Fill table rows from a list of products
    private void populateTable(List<Product> products) {
        DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
        model.setRowCount(0); // Clear first
        for (Product p : products) {
            model.addRow(new Object[]{
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategoryName(),
                    p.getSize(),
                    p.getColor(),
                    String.format("%.2f", p.getPrice()),
                    p.getQuantity()
            });
        }
    }

    // When user clicks a table row, fill the fields above
    private void setupTableClickListener() {
        tblProducts.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblProducts.getSelectedRow();
                if (row == -1) return;

                DefaultTableModel model = (DefaultTableModel) tblProducts.getModel();
                selectedProductId = (int) model.getValueAt(row, 0);
                txtProductName.setText(model.getValueAt(row, 1).toString());
                txtColor.setText(model.getValueAt(row, 4).toString());
                txtPrice.setText(model.getValueAt(row, 5).toString());
                txtQuantity.setText(model.getValueAt(row, 6).toString());

                // Set size dropdown
                String size = model.getValueAt(row, 3).toString();
                cmbSize.setSelectedItem(size);

                // Set category dropdown by matching name
                String category = model.getValueAt(row, 2).toString();
                for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                    Category c = (Category) cmbCategory.getItemAt(i);

                    if (c.getCategoryName().equals(category)) {
                        cmbCategory.setSelectedIndex(i);
                        break;
                    }
                }
            }
        });
    }

    // Button actions
    private void setupButtons() {
        // Add
        btnAdd.addActionListener(e -> {
            if (!validateFields()) return;

            Product p = buildProductFromFields();
            boolean success = productDAO.addProduct(p);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Product added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllProducts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add product!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update
        btnUpdate.addActionListener(e -> {
            if (selectedProductId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a product form the table first.",
                        "No Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!validateFields()) return;

            Product p = buildProductFromFields();
            p.setProductId(selectedProductId);
            boolean success = productDAO.updateProduct(p);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Product updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllProducts();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update te product.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete
        btnDelete.addActionListener(e -> {
            if (selectedProductId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a product from the table first.",
                        "No Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this product?",
                    "Confirm", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = productDAO.deleteProduct(selectedProductId);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Product deleted successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllProducts();
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to delete product.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Search
        btnSearch.addActionListener(e -> {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                loadAllProducts();
            } else {
                populateTable(productDAO.searchProducts(keyword));
            }
        });

        // Also search when pressing Enter in the search field
        txtSearch.addActionListener(e -> btnSearch.doClick());

        // Clear
        btnClear.addActionListener(e -> clearFields());
    }

    // Build a Product object from the current field values
    private Product buildProductFromFields() {
        Product p = new Product();
        p.setProductName(txtProductName.getText().trim());
        p.setSize(cmbSize.getSelectedItem().toString());
        p.setColor(txtColor.getText().trim());
        p.setPrice(new BigDecimal(txtPrice.getText().trim()));
        p.setQuantity(Integer.parseInt(txtQuantity.getText().trim()));
        p.setLowStockThreshold(5); // default threshold

        Category selectedCategory = (Category) cmbCategory.getSelectedItem();
        if (selectedCategory != null) {
            p.setCategoryId(selectedCategory.getCategoryId());
        }
        return p;
    }

    // Validate that all required are filled in
    private boolean validateFields() {
        if (txtProductName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Product name is required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtProductName.requestFocus();
            return false;
        }

        if (txtPrice.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Price is required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPrice.requestFocus();
            return false;
        }

        try {
            new BigDecimal(txtPrice.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Price must be a valid number (e.g. 12.50).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtPrice.requestFocus();
            return false;
        }

        if (txtQuantity.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Quantity is required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return false;
        }

        try {
            Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be a whole number (e.g. 10).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return false;
        }
        return true;
    }

    // Clear all input fields and reset selection
    private void clearFields() {
        txtProductName.setText("");
        txtColor.setText("");
        txtPrice.setText("");
        txtQuantity.setText("");
        txtSearch.setText("");
        cmbSize.setSelectedIndex(0);
        cmbCategory.setSelectedIndex(0);
        selectedProductId = -1;
        tblProducts.clearSelection();
    }

    private void createUIComponents() {

    }
}

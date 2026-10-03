package com.posapp.ui;

import com.posapp.dao.ProductDAO;
import com.posapp.dao.SaleDAO;
import com.posapp.model.Employee;
import com.posapp.model.Product;
import com.posapp.model.Sale;

import javax.swing.*;
import javax.swing.BoxLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashboardForm extends JFrame {
    private JPanel mainPanel;
    private JPanel panelSidebar;
    private JPanel panelContent;
    private JLabel lblName;
    private JButton btnDashboard;
    private JButton btnProducts;
    private JButton btnCustomers;
    private JButton btnSales;
    private JButton btnInventory;
    private JButton btnReports;
    private JButton btnLogout;
    private JPanel panelTop;
    private JPanel panelCards;
    private JPanel panelTable;
    private JTable tblRecentSales;
    private JLabel lblDateTime;
    private JLabel lblWelcome;
    private JPanel lblTotalSales;
    private JPanel lblTotalRevenue;
    private JPanel lblTotalProducts;
    private JPanel lblLowStock;
    private JLabel lblSalesCount;
    private JLabel lblRevenueAmount;
    private JLabel lblProductsCount;
    private JLabel lblLowStockCount;
// _________________________________________________________________

    private final Employee loggedInEmployee;
    private final SaleDAO saleDAO = new SaleDAO();
    private final ProductDAO productDAO = new ProductDAO();

    public DashboardForm(Employee employee) {
        this.loggedInEmployee = employee;

        setTitle("Clothing Store POS System - Dashboard");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        panelSidebar.setPreferredSize(new Dimension(200, 650));
        panelSidebar.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));

        panelContent.setMaximumSize(new Dimension(800, 0));
        panelContent.setPreferredSize(new Dimension(800, 0));
        // key: override panel content's GridLayoutManager with BorderLayout
        // So we can freely add/ remove components inside it
        panelContent.setLayout(new BorderLayout());
        // Wrap original dashboard panels into one panel
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBackground(new Color(245, 245, 245));
        dashboardPanel.add(panelTop);
        dashboardPanel.add(panelCards);
        dashboardPanel.add(panelTable);
        panelContent.add(dashboardPanel, BorderLayout.CENTER);

        setupSidebarButtons();
        setupRecentSalesTable();
        applyRoleAccess();
        loadDashboardData();
        startClock();

        // Show welcome message with employee name
        lblWelcome.setText("Welcome, " + loggedInEmployee.getFullName() + " | "
                + loggedInEmployee.getRole());
    }

    // - Role-based access: hide buttons Cashier shouldn't see
    private void applyRoleAccess() {
        if (!loggedInEmployee.isAdmin()) {
            btnProducts.setVisible(false);
            btnCustomers.setVisible(false);
            btnInventory.setVisible(false);
            btnReports.setVisible(false);
        }
    }

    // - Load real data from database cards and table
    private void loadDashboardData() {
        // 1. Today's sales count + revenue
        Object[] todaySummary = saleDAO.getTodaySummary();
        int salesCount = (int) todaySummary[0];
        BigDecimal revenue = (BigDecimal) todaySummary[1];

        lblSalesCount.setText(String.valueOf(salesCount));
        lblRevenueAmount.setText("$" + String.format("%,.2f", revenue));

        // 2. Total product in stock
        List<Product> allProducts = productDAO.getAllProducts();
        lblProductsCount.setText(String.valueOf(allProducts.size()));

        // 3. Low Stock count
        List<Product> lowStock = productDAO.getLowStockProducts();
        lblLowStockCount.setText(String.valueOf(lowStock.size()));

        // Change low stock label red if there are any alerts
        if (!lowStock.isEmpty()) {
            lblLowStockCount.setForeground(new Color(198, 40, 40));
        }

        // 4. Recent sales table
        loadRecentSales();
    }

    // - Set up the JTable columns
    private void setupRecentSalesTable() {
        String[] columns = {"invoice No", "Customer", "Total", "Date", "Payment"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        tblRecentSales.setModel(model);

        // Style the table
        tblRecentSales.setRowHeight(28);
        tblRecentSales.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tblRecentSales.getTableHeader().setBackground(new Color(26, 58, 107));
        tblRecentSales.getTableHeader().setForeground(Color.white);
        tblRecentSales.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tblRecentSales.setSelectionBackground(new Color(230, 240, 250));
        tblRecentSales.setGridColor(new Color(230, 230, 230));


        // Column widths
//        tblRecentSales.getColumnModel().getColumn(0).setPreferredWidth(120); // Invoice No
//        tblRecentSales.getColumnModel().getColumn(1).setPreferredWidth(160); // Customer
//        tblRecentSales.getColumnModel().getColumn(2).setPreferredWidth(120);  // Total
//        tblRecentSales.getColumnModel().getColumn(3).setPreferredWidth(160); // Date
//        tblRecentSales.getColumnModel().getColumn(4).setPreferredWidth(120); // Payment
    }

    // Fill recent sales table with last 10 sales
    private void loadRecentSales() {
        DefaultTableModel model = (DefaultTableModel) tblRecentSales.getModel();
        model.setRowCount(0);

        List<Sale> recentSales = saleDAO.getRecentSales(10);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Sale sale : recentSales) {
            String dateStr = sale.getSaleDate() != null
                    ? sdf.format(sale.getSaleDate())
                    : "-";
            String customer = sale.getCustomerName() != null
                    ? sale.getCustomerName()
                    : "-";
            model.addRow(new Object[]{
                    sale.getInvoiceNo(),customer,
                    String.format("%,.2f", sale.getGrandTotal()),
                    dateStr,sale.getPaymentMethod()
            });
        }
    }

    // Sidebar button action
    private void setupSidebarButtons() {
        // Highlight Dashboard button as active on load
        setActiveButton(btnDashboard);

        btnDashboard.addActionListener(e -> {
            setActiveButton(btnDashboard);
            panelContent.removeAll();
            JPanel dashboardPanel = new JPanel();
            dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
            dashboardPanel.setBackground(new Color(245, 245, 245));
            dashboardPanel.add(panelTop);
            dashboardPanel.add(panelCards);
            dashboardPanel.add(panelTable);
            panelContent.add(dashboardPanel, BorderLayout.CENTER);
            panelContent.revalidate();
            panelContent.repaint();
            loadDashboardData();
        });

        btnProducts.addActionListener(e -> {
            setActiveButton(btnProducts);
            panelContent.removeAll();
            panelContent.add(new ProductForm(), BorderLayout.CENTER);
            panelContent.revalidate();
            panelContent.repaint();
        });

        btnCustomers.addActionListener(e -> {
            setActiveButton(btnCustomers);
            panelContent.removeAll();
            panelContent.add(new CustomerForm(), BorderLayout.CENTER);
            panelContent.revalidate();
            panelContent.repaint();
        });

        btnSales.addActionListener(e -> {
            setActiveButton(btnSales);
            panelContent.removeAll();
            panelContent.add(new SalesPOSForm(loggedInEmployee.getEmployeeId()), BorderLayout.CENTER);
            panelContent.revalidate();
            panelContent.repaint();
        });

        btnInventory.addActionListener(e -> {
            setActiveButton(btnInventory);
            panelContent.removeAll();
            panelContent.add(new InventoryForm(), BorderLayout.CENTER);
            panelContent.revalidate();
            panelContent.repaint();
        });

        btnReports.addActionListener(e -> {
            setActiveButton(btnReports);
            panelContent.removeAll();
            panelContent.add(new ReportForm(), BorderLayout.CENTER);
            panelContent.revalidate();
            panelContent.repaint();
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?", "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginForm().setVisible(true);
                this.dispose();
            }
        });
    }

    // Highlights the active sidebar button
    private void setActiveButton(JButton activeBtn) {
        JButton[] allButtons = {btnDashboard, btnProducts, btnCustomers,
                btnSales, btnInventory, btnReports, btnLogout};
        for (JButton btn : allButtons) {
            btn.setBackground(new Color(26, 58, 107)); // Default dark blue
            btn.setForeground(Color.white);
        }
        // Highlight the active one slightly lighter
        activeBtn.setBackground(new Color(52, 90, 150));
    }

    // Live clock that updates every second
    private void startClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    lblDateTime.setText(sdf.format(new Date()));
                });
            }
        }, 0, 1000);
    }



    // Entry point for testing Dashboard standalone
    public static void main(String[] args) {
        // Create a fake admin employee for testing
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId(1);
        testEmployee.setUsername("admin");
        testEmployee.setFullName("Administrator");
        testEmployee.setRole("Admin");

        SwingUtilities.invokeLater(() -> {
            DashboardForm form = new DashboardForm(testEmployee);
            form.setVisible(true);
        });
    }
}

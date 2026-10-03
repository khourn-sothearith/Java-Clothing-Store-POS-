# 🛍️ Clothing Store POS Management System

A desktop Point of Sale (POS) system for a clothing store, built with Java Swing, JDBC, and MySQL. Developed as a final project for the Information Technology Engineering program at the Royal University of Phnom Penh (RUPP).

---

## 📋 Table of Contents

- [About](#about)
- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [Database Setup](#database-setup)
- [How to Run](#how-to-run)
- [Screenshots](#screenshots)
- [Author](#author)

---

## About

This system helps a clothing store manage daily operations including employee login, product management, sales transactions, inventory tracking, and reporting.

**When a customer buys clothes:**
1. Cashier logs in with their account
2. Searches and selects products
3. Adds products to the cart
4. Applies VIP customer discount (if applicable)
5. Accepts payment (Cash / ABA Pay / Wing Pay)
6. Generates and prints a receipt
7. Stock is automatically updated in the database

---

## Features

### Basic Features
- ✅ Secure employee login with role-based access
- ✅ Product CRUD (Add, Update, Delete, Search)
- ✅ Customer CRUD with VIP discount support
- ✅ Sales POS with cart system
- ✅ Auto stock reduction after each sale
- ✅ MySQL database with JDBC connection

### Advanced Features
- ⭐ Role-based access (Admin vs Cashier)
- ⭐ VIP customer discount system
- ⭐ Receipt printing via system print dialog
- ⭐ Low stock alerts with red row highlighting
- ⭐ Daily & monthly sales reports
- ⭐ Top selling products report
- ⭐ Customer search by name or phone number
- ⭐ Live clock on dashboard
- ⭐ Local Cambodian payment methods (ABA Pay, Wing Pay)

---

## Technologies

| Layer | Technology |
|---|---|
| GUI | Java Swing + IntelliJ GUI Designer |
| Backend | Java 25 |
| Database | MySQL 8 |
| DB Connection | JDBC (mysql-connector-j 9.7.0) |
| IDE | IntelliJ IDEA 2025 |
| Architecture | 3-Layer: Model → DAO → UI |

---

## Project Structure

```
ClothingStorePOS/
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── src/
│   └── com/
│       └── posapp/
│           ├── Main.java                  ← Entry point
│           ├── db/
│           │   └── DBConnection.java
│           ├── model/
│           │   ├── Employee.java
│           │   ├── Category.java
│           │   ├── Product.java
│           │   ├── Customer.java
│           │   ├── Sale.java
│           │   └── SaleDetail.java
│           ├── dao/
│           │   ├── EmployeeDAO.java
│           │   ├── CategoryDAO.java
│           │   ├── ProductDAO.java
│           │   ├── CustomerDAO.java
│           │   └── SaleDAO.java
│           └── ui/
│               ├── LoginForm.java / .form
│               ├── DashboardForm.java / .form
│               ├── ProductForm.java / .form
│               ├── CustomerForm.java / .form
│               ├── SalesPOSForm.java / .form
│               ├── InventoryForm.java / .form
│               └── ReportForm.java / .form
└── resources/
    ├── logo.png
    └── clothes.png
```

---

## Database Setup

### 1. Install MySQL
Download and install [MySQL Community Server](https://dev.mysql.com/downloads/installer/).

### 2. Run the schema script
Open MySQL Workbench and run `clothing_pos_schema.sql`:

```sql
CREATE DATABASE IF NOT EXISTS clothing_pos_db;
USE clothing_pos_db;
```

The script creates all 6 tables and inserts sample data automatically.

### 3. Default login accounts

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin |
| `cashier1` | `cashier123` | Cashier |

---

## How to Run

### Prerequisites
- JDK 17 or higher
- IntelliJ IDEA
- MySQL Server running

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/your-username/ClothingStorePOS.git
```

2. **Open in IntelliJ IDEA**
   - File → Open → select the project folder

3. **Add MySQL Connector**
   - Copy `mysql-connector-j-9.7.0.jar` into the `lib/` folder
   - Right-click the JAR → Add as Library

4. **Configure database password**
   - Open `src/com/posapp/db/DBConnection.java`
   - Update `DB_PASSWORD` with your MySQL root password

5. **Run the database script**
   - Open MySQL Workbench
   - Run `clothing_pos_schema.sql`

6. **Run the application**
   - Run `com.posapp.Main`
   - Login with `admin` / `admin123`

---

## Role-Based Access

| Module | Admin | Cashier |
|---|---|---|
| Dashboard | ✅ | ✅ |
| Products | ✅ | ❌ |
| Customers | ✅ | ❌ |
| Sales POS | ✅ | ✅ |
| Inventory | ✅ | ❌ |
| Reports | ✅ | ❌ |

---

## Database Schema

```
employee    → login and role management
category    → product categories
product     → clothing items (name, size, color, price, stock)
customer    → customer records with VIP discount
sale        → invoice header per transaction
sale_detail → line items per sale
```

---

## Author

**Rith**
Year 2 Information Technology Engineering
Royal University of Phnom Penh (RUPP)

---

## License

This project was developed for academic purposes as a final Java course project.

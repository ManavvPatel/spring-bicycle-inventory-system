CREATE DATABASE TrentBicycleShopDB;
USE TrentBicycleShopDB;

-- Users Table (For Authentication & Role Management)
CREATE TABLE Users (
    UserID INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(50) UNIQUE NOT NULL,
    HashedPassword VARCHAR(255) NOT NULL,
    Role ENUM('Admin', 'Owner', 'Employee') NOT NULL,
    CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Customer Table
CREATE TABLE Customer (
    CustomerID INT PRIMARY KEY AUTO_INCREMENT,
    FirstName VARCHAR(50) NOT NULL,
    LastName VARCHAR(50) NOT NULL,
    Phone VARCHAR(15),
    Email VARCHAR(100) UNIQUE
);

-- Supplier Table
CREATE TABLE Supplier (
    SupplierID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierName VARCHAR(100) NOT NULL,
    ContactRep VARCHAR(100),
    Phone VARCHAR(15),
    Email VARCHAR(100)
);

-- Status Table
CREATE TABLE Status (
    StatusID INT PRIMARY KEY AUTO_INCREMENT,
    StatusName VARCHAR(50) NOT NULL
);

-- Categories Table
CREATE TABLE Categories (
    CategoryID INT PRIMARY KEY AUTO_INCREMENT,
    CategoryName VARCHAR(100) NOT NULL
);

-- Products Table
CREATE TABLE Products (
    ProductID INT PRIMARY KEY AUTO_INCREMENT,
    ProductName VARCHAR(100) NOT NULL,
    CategoryID INT,
    Brand VARCHAR(100),
    UnitPrice DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (CategoryID) REFERENCES Categories(CategoryID) ON DELETE SET NULL
);

-- Inventory Table
CREATE TABLE Inventory (
    InventoryID INT PRIMARY KEY AUTO_INCREMENT,
    ProductID INT NOT NULL,
    CurrentStock INT NOT NULL DEFAULT 0,
    LastUpdate TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE
);

-- Sales Table
CREATE TABLE Sales (
    SalesID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerID INT NOT NULL,
    SaleDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TotalAmount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE
);

ALTER TABLE Sales MODIFY TotalAmount DECIMAL(10,2) NOT NULL DEFAULT 0;

-- SalesDetails Table
CREATE TABLE SalesDetails (
    SalesDetailsID INT PRIMARY KEY AUTO_INCREMENT,
    SalesID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(10,2) NOT NULL,
    SubTotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (SalesID) REFERENCES Sales(SalesID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE
);

-- Orders Table
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY AUTO_INCREMENT,
    SupplierID INT NOT NULL,
    OrderDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    StatusID INT,
    FOREIGN KEY (SupplierID) REFERENCES Supplier(SupplierID) ON DELETE CASCADE,
    FOREIGN KEY (StatusID) REFERENCES Status(StatusID) ON DELETE SET NULL
);

-- OrderDetails Table
CREATE TABLE OrderDetails (
    OrderDetailsID INT PRIMARY KEY AUTO_INCREMENT,
    OrderID INT NOT NULL,
    ProductID INT NOT NULL,
    Quantity INT NOT NULL,
    FOREIGN KEY (OrderID) REFERENCES Orders(OrderID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE
);

-- Returns Table
CREATE TABLE Returns (
    ReturnID INT PRIMARY KEY AUTO_INCREMENT,
    SaleID INT NOT NULL,
    ProductID INT NOT NULL,
    ReturnDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    RefundAmount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (SaleID) REFERENCES Sales(SalesID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE
);

-- RepairServices Table
CREATE TABLE RepairServices (
    ServiceID INT PRIMARY KEY AUTO_INCREMENT,
    ServiceName VARCHAR(100) NOT NULL,
    Description TEXT,
    Cost DECIMAL(10,2) NOT NULL
);
 
-- Insert sample services
INSERT INTO RepairServices (ServiceName, Description, Cost) VALUES
('Brake Adjustment', 'Adjustment of the brake system for safe ride.', 25.00),
('Gear Tune-Up', 'Complete gear maintenance for smooth shifting.', 30.00),
('Wheel True', 'Truing of wheels for improved ride quality.', 35.00),
('Tire Replacement', 'Replacement of worn or damaged tires with new ones.', 40.00),
('Chain Replacement', 'Replacement and lubrication of the chain.', 20.00),
('Full Bike Overhaul', 'Comprehensive inspection and servicing of all bike components.', 100.00);

-- ServiceTransactions Table
CREATE TABLE ServiceTransactions (
    ServiceTransactionID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerID INT NOT NULL,
    ServiceID INT NOT NULL,
    TransactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE,
    FOREIGN KEY (ServiceID) REFERENCES RepairServices(ServiceID) ON DELETE CASCADE
);

INSERT INTO Categories (CategoryName)
VALUES 
('Bikes'),
('Parts'),
('Wheels'),
('Accessories'),
('Clothing'),
('Helmets'),
('Shoes');

INSERT INTO Status (StatusName)
VALUES ('Shipped'),
('Received');

Trent Bicycle Shop Management System

A full-stack enterprise management solution designed to automate inventory tracking, supplier procurement, and retail sales operations. This system focuses on data integrity, system security, and cloud scalability.
Technical Stack

    Backend: Java 21, Spring Boot 3, Spring Data JPA, Spring Security

    Frontend: Thymeleaf, HTML5, CSS3, JavaScript

    Database: MySQL 8.0 (Production: Aiven Managed Cloud)

    DevOps: Docker, Docker Compose, Google Cloud Platform (GCP)

    Build Tool: Maven

System Architecture and Design

The application follows a decoupled architecture, separating the business logic from the managed cloud database to ensure high availability and performance even on resource-constrained hardware.
1. Database Schema (ERD)

The backend is supported by a complex relational schema featuring over 14 entities. This design ensures strict data normalization and referential integrity across sales and inventory modules.

<img width="1023" height="724" alt="image" src="https://github.com/user-attachments/assets/17267a17-5fb0-44c4-a292-710c7263405d" />


Key Entities:

    Core: Products, Inventory, Suppliers

    Transactions: Orders, Sales, Returns

    Users: Customers, Employees (Role-Based Access Control)

2. Frontend and User Experience

The user interface is built for efficiency, providing store managers and staff with a real-time overview of stock levels, financial transactions, and repair service statuses.

<img width="1247" height="453" alt="image" src="https://github.com/user-attachments/assets/91ab6781-448d-40df-a7c7-2fb7f25cebac" />

Key Features

    1. Role-Based Access Control (RBAC): Secured endpoints using Spring Security to manage permissions between Admin and Staff roles, ensuring sensitive data is only accessible to authorized users.

    2. Automated Inventory Tracking: Real-time monitoring of stock levels with automated status updates for products requiring restocking.

    3. Supplier Management: Integrated procurement workflow to manage external supplier contact information and purchase order history.

    4. Repair Services Integration: Tracking module for service transactions, linking customers to specific repair types and assigned staff.

Deployment and DevOps

This project is fully containerized and deployed on a Google Cloud Platform (GCP) Compute Engine instance.

    Containerization: Multi-stage Docker builds are utilized to minimize final image size and reduce the attack surface for improved security.

    Orchestration: Docker Compose manages the application environment, network bridging, and container lifecycles.

    Managed Database: Utilizes Aiven MySQL to offload database processing from the application server, providing automated backups and managed scaling.

Local Setup

To run this project locally, ensure you have Docker and Git installed on your machine.

    Clone the repository:
    Bash

    git clone https://github.com/ManavvPatel/spring-bicycle-inventory-system.git

    Navigate to the project directory:
    Bash

    cd spring-bicycle-inventory-system

    Configure your database credentials in the docker-compose.yml file.

    Launch the application using the Maven Wrapper and Docker:
    Bash

    docker compose up --build

    Access the application at http://localhost:8080.

Login via :- 
Username: Admin
Password: admin123

Username: Owner
Password: owner123

Username: Employee
Password: employee123

Role Access:
Admins can create new users.
Admin Cannot access Suppliers or Orders.
Owner can access all resources.
Empoyees cannot access Suppliers, Orders and User Accounts.

Documentation and References

The project structure includes a docs folder containing the original project report, detailed diagrams, and technical specifications used during the development phase.

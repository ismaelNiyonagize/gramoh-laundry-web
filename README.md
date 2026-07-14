# Gramoh Laundry Management System

A full-stack web application designed to digitize and automate laundry operations by managing customers, orders, payments, laundry status tracking, and delivery workflows.

## Overview

Gramoh Laundry Management System was developed to solve operational challenges faced by a growing laundry service business. The system replaces manual tracking processes with a centralized digital platform that improves order visibility, reduces errors, and helps manage the complete laundry workflow from customer registration to delivery.

The application was designed using a user-centered approach, where operational feedback was collected and used to continuously improve system functionality and usability.

## Key Features

### Customer Management
- Register and manage customer information
- Maintain customer records
- Track customer service history

### Order Management
- Create and manage laundry orders
- Assign order status throughout the cleaning process
- Track order progress from receiving clothes to delivery

### Laundry Workflow Tracking
- Monitor laundry stages
- Improve visibility of ongoing operations
- Reduce dependency on manual records

### Payment Management
- Record customer payments
- Track payment status
- Maintain transaction records

### Pickup and Delivery Management
- Manage pickup requests
- Track delivery information
- Support better coordination of logistics

### Database Management
- Store customer, order, and operational data securely
- Provide structured access to business information

---

# System Architecture

The application follows a layered software architecture:
            User Interface
                 |
                 |
          Controller Layer
                 |
                 |
           Service Layer
                 |
                 |
        Repository Layer
                 |
                 |
          Database Layer
          
### Architecture Components

**Controller Layer**
- Handles HTTP requests and responses
- Provides communication between users and the application

**Service Layer**
- Contains business logic
- Processes application workflows

**Repository Layer**
- Manages database interactions
- Performs CRUD operations

**Database Layer**
- Stores application data including customers, orders, and payments

---

# Technologies Used

## Backend

- Java
- Spring Boot
- Spring MVC
- Spring Data JPA

## Frontend

- HTML5
- CSS3
- JavaScript
- Thymeleaf

## Database

- MySQL

## Development Tools

- Maven
- Git
- GitHub
- IntelliJ IDEA

---

# Development Highlights

- Designed and developed a complete business management application from concept to deployment.
- Built backend services using Java Spring Boot.
- Designed relational database structures for managing operational data.
- Implemented CRUD operations for business entities.
- Developed user workflows based on real operational requirements.
- Applied software engineering principles including separation of concerns and maintainable code structure.
- Tested and improved system functionality based on user feedback.

---

# Installation and Setup

## Prerequisites

Before running the application, ensure you have installed:

- Java JDK 17 or higher
- Maven
- MySQL Server
- Git

---

## Clone Repository

```bash
git clone https://github.com/ismaelNiyonagize/gramoh-laundry-web.git
cd gramoh-laundry-web
CREATE DATABASE gramoh;

src/main/resources/application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/gramoh
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

mvn spring-boot:run
http://localhost:8080


===========Project Structure =============
gramoh-laundry-web
│
├── src/main/java
│   └── com.gramoh
│       ├── controller
│       ├── service
│       ├── repository
│       ├── model
│       └── configuration
│
├── src/main/resources
│   ├── templates
│   ├── static
│   └── application.properties
│
├── pom.xml
└── README.md

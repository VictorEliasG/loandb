# Loan Management System

A RESTful back-end application for managing user loans with role-based access control. Built using Java 17, Javalin, and PostgreSQL.

## Database Configuration

### PostgreSQL Password:

123456

### 2. Create PostgreSQL Database:

loandb

### 3. Database creation

(loandb.jar) creates tables with sample records automatically

## Installation & Setup

### Clone the repository:

git clone https://github.com/victoreliasg/loandb

### Build with Maven

mvn clean package

### Run the application:

loandb.jar

### API Endpoints using POSTMAN

- Protect endpoints using sessions:
  - **Logged-in** check for basic user actions (view/edit own loans).
  - **Manager** check for managerial actions (view all loans, approve/reject).
- Common endpoints might include:
  - **POST** `/auth/register` – Register a new user.
  - **POST** `/auth/login` – Log in a user.
  - **POST** `/auth/logout` – Log out the current session.
  - **GET** `/users/{id}` – Get user info (user can only see their own, or manager can see any user).
  - **PUT** `/users/{id}` – Update user profile (only if it’s the same user or a manager).
  - **POST** `/loans` – Create a loan (logged-in user).
  - **GET** `/loans` – View all loans (manager only) or just the user’s loans (regular user). You can implement this in two separate endpoints or a single endpoint with conditional filtering.
  - **GET** `/loans/{loanId}` – View a specific loan (owner or manager).
  - **PUT** `/loans/{loanId}` – Update the loan (owner or manager).
  - **PUT** `/loans/{loanId}/approve` – Approve a loan (manager only).
  - **PUT** `/loans/{loanId}/reject` – Reject a loan (manager only).

### Features

- **User Authentication & Authorization**

  - Register, login, and logout endpoints.
  - Session management (cookie based).
  - Roles: Regular User (manage own loans) and Manager (approve/reject all loans).

- **User Profile Management**

  - View/update profile (users can only modify their own data).

- **Loan Management**

  - Regular Users: Create, view, and edit their loans.
  - Managers: View all loans, approve/reject loans.
  - Loan fields: amount, type, status (pending/approved/rejected).

- **Data Persistence**
  - PostgreSQL database with `users` and `loans` tables.
  - Password hashing for security.

## Technologies

- **Java 17**
- **Javalin**: REST API framework.
- **PostgreSQL**: Database storage.
- **JDBC**: Database connectivity.
- **JUnit 5**: Unit testing.
- **Logback**: Logging (console/file).
- **Postman**: API testing.

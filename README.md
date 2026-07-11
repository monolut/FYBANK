# FYBANK

A banking backend built with a microservice architecture using Spring Boot.

The project demonstrates secure authentication, account management, balance operations, money transfers, and communication between independent services. It is designed as a learning project to explore distributed backend development and clean service separation.

---

# Features

## Authentication

- User registration
- User authentication
- JWT access tokens
- Refresh tokens
- Password update
- Role-based authorization
- BCrypt password hashing

## User Management

- Create users
- Retrieve user information
- Update user data
- Delete users
- Integration with authentication and account services

## Account Management

- Create bank accounts
- Generate unique IBANs
- Retrieve account information
- Close accounts
- Account status management

## Balance Operations

- Deposit funds
- Withdraw funds
- Reserved balance support
- Available balance calculation

## Money Transfers

- Transfer between internal accounts
- Transfer by IBAN
- Transaction history
- Transaction reference generation
- Transfer validation
- Insufficient funds protection

---

# Architecture

```
                Client
                   │
     ┌─────────────┼─────────────┐
     │             │             │
     ▼             ▼             ▼
 Auth Service   User Service   Account Service
      │               │               │
      └───────────────┼───────────────┘
                      │
               Shared Auth Library
                      │
                 PostgreSQL
```

---

# Services

## Auth Service

Responsible for authentication and authorization.

Main responsibilities:

- User registration
- Login
- JWT generation
- Refresh token management
- Password updates
- User role management

---

## User Service

Responsible for user profiles and business information.

Main responsibilities:

- User management
- Communication with Auth Service
- Communication with Account Service
- User lifecycle management

---

## Account Service

Responsible for banking operations.

Main responsibilities:

- Bank account management
- Balance management
- Internal transfers
- IBAN transfers
- Transaction processing
- Transaction history

---

## Auth Common Library

Shared library used across services.

Contains:

- JWT authentication filter
- JWT service
- Shared authentication utilities

---

# Tech Stack

### Backend

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- OpenFeign
- Maven

### Database

- PostgreSQL

### Authentication

- JWT
- Refresh Tokens
- BCrypt

---

# Project Structure

```
FYBANK
│
├── libs
│   └── auth-common
│
├── services
│   ├── auth-service
│   ├── user-service
│   └── account-service
│
└── pom.xml
```

---

# Main Components

### Authentication

- JWT authentication
- Refresh token support
- Shared authentication library

### User Management

- User CRUD operations
- Service integration

### Banking

- Account creation
- Balance management
- Money transfers
- Transaction records
- IBAN generation

---

# Exception Handling

Each service provides centralized exception handling using global exception handlers.

Examples include:

- User not found
- Account not found
- Insufficient funds
- Forbidden operations
- Duplicate email
- Invalid transfer requests

---

# Build

Clone the repository:

```bash
git clone https://github.com/monolut/FYBANK.git
```

Build the project:

```bash
mvn clean install
```

---

# Run

Each microservice can be started independently.

Typical startup order:

1. Auth Service
2. User Service
3. Account Service

Configure PostgreSQL connection for every service using its
`application.properties` file.

---

# Project Goals

This project was created to practice:

- Microservice architecture
- Spring Boot
- Spring Security
- JWT authentication
- REST API design
- Spring Data JPA
- OpenFeign
- Service communication
- Banking domain modeling
- Clean architecture principles

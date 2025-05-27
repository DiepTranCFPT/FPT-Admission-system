# FPT Admission System

## Overview

The **FPT Admission System** is a backend service designed to manage user authentication, registration, and administration for an admissions platform. It leverages Spring Boot, Spring Security, and integrates with Firebase for authentication, providing a robust and secure system for handling user accounts and roles.

## Features

- **User Authentication & Registration**
  - Register and login with email/phone and password
  - Support for Google OAuth via Firebase
  - Secure password encoding and validation

- **Admin Management**
  - Admin account creation and management via `AdminService`
  - Role-based authorization using Spring Security

- **Account Utilities**
  - Random code generation for account validation
  - Email and phone validation utilities

- **Security**
  - JWT-based authentication and authorization
  - Custom security configuration with public and protected endpoints
  - Password encryption using BCrypt

- **User Management**
  - Enable/disable user accounts
  - Edit and delete (soft delete) users
  - Retrieve all users with role and status information

## Tech Stack

- Java
- Spring Boot
- Spring Security
- JPA/Hibernate
- Firebase Authentication
- JWT (JSON Web Token)
- Maven

## Project Structure

- `entity/`: Domain models (User, Post, AccountDetails, etc.)
- `repository/`: Data access layers
- `service/`: Business logic and authentication services
- `config/`: Security and application configuration
- `utils/`: Utility classes for validation and account operations

## Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DiepTranCFPT/FPT-Admission-system.git

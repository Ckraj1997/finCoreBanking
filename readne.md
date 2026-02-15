# 🏦 Core Banking Management System (CBMS)

A full-stack **Core Banking Management System** developed as an **MCA final-year project**, implementing real-world banking workflows such as account management, transactions, loans, beneficiaries, approvals, security, and reporting.

---

## 📌 Project Overview

The **Core Banking Management System (CBMS)** simulates essential banking operations with **role-based access control**, **approval workflows**, and **secure transaction handling**.  
The system is designed using **clean layered architecture** and follows **industry-standard best practices**.

---

## 🛠️ Technology Stack

### Backend

- Java  
- Spring Boot  
- Spring Security  
- Spring Data JPA (Hibernate)  

### Frontend

- Thymeleaf (Server-Side Rendering)  
- Bootstrap 5  

### Database

- MySQL  

### Build & Tools

- Maven  
- VS Code  

---

## 🧱 Architecture

The application follows a **Layered MVC Architecture**:

### Architecture Rules

- ❌ Controllers never access repositories directly  
- ✅ Controllers interact only with services  
- ✅ Services handle business logic and repository access  
- ❌ DTOs are never injected as Spring beans  
- ❌ No hardcoded business rules  
- ✅ Authentication is handled using `Authentication`, not `Principal`  

---

## 🔐 Security Features

- Spring Security based authentication
- Custom login page (default login disabled)
- Role-based access:
  - `CUSTOMER`
  - `ADMIN`
- Role-based UI rendering using Thymeleaf Security dialect
- Failed login attempt tracking
- Account lock on suspicious activity
- Fraud detection logging
- Custom authentication success & failure handlers

---

## 📦 Modules Implemented

### 1️⃣ User Management

- User registration
- Login & logout
- Role-based dashboards
- Failed login attempt handling

### 2️⃣ Account Management

- Account listing using **interactive cards**
- System-generated account numbers
- Account creation request by customer
- Account status:
  - `PENDING`
  - `ACTIVE`
- **Admin approval required**
- Account details page:
  - Balance summary
  - Recent transactions
  - Notifications
  - Interest calculation page

### 3️⃣ Transaction Management

- Credit / Debit (account selectable)
- Fund transfer (approved beneficiaries only)
- Pagination and filtering
- Transaction receipt page
- PDF & CSV export
- Dashboard recent transactions
- Atomic operations using `@Transactional`

### 4️⃣ Beneficiary Management

- Add beneficiary by customer
- Status:
  - `PENDING`
  - `APPROVED`
  - `REJECTED`
- Admin approval workflow
- Only approved beneficiaries usable for transfer

### 5️⃣ Loan Management

- Loan application by customer
- Admin approval / rejection
- Loan status tracking
- Interest rate managed via database
- Admin can enable / disable loan types
- Existing approved loans remain unaffected
- EMI:
  - Calculated dynamically
  - Displayed only for approved loans
- Dedicated EMI calculator page

### 6️⃣ Dashboard

#### Customer Dashboard

- Account summary
- Recent transactions

#### Admin Dashboard

- User statistics
- Loan statistics
- Pending account approvals
- Beneficiary approvals
- Loan approvals

### 7️⃣ Reports

- Transaction statements
- Export to PDF / CSV

### 8️⃣ Notifications

- Account-specific notifications
- Transaction alerts
- Approval notifications

### 9️⃣ Fraud Detection

- Suspicious login detection
- Failed attempt monitoring

### 🔟 Audit Logs

- Critical user & admin actions tracked

---

## 🗂️ Loan Interest Configuration

Loan interest rates are managed using the following entity:

```java
LoanInterestRate {
    loanType;
    interestRate;
    active;
}


---

## ✅ What You Can Do Next

- 📁 Save this as `README.md` in your project root  
- 🧾 Add screenshots section (optional)
- 📊 Add ER / DFD diagrams later if needed

If you want, I can:
1️⃣ Shorten this for GitHub  
2️⃣ Add screenshots placeholders  
3️⃣ Convert it into **project report format**

Key Points

Admin can enable/disable loan types

Interest rate is copied into the Loan at application time

Existing approved loans are never affected by later changes

EMI calculations always use stored loan interest rate

🎨 UI Design Principles

Bootstrap-based responsive design

Card-based dashboards

Tables for detailed data

Status badges (ACTIVE, PENDING, etc.)

Sidebar + dashboard navigation

No business logic inside views

🎓 Academic & Viva Notes

Designed for clarity and correctness

Emphasis on:

Separation of concerns

Security

Extensibility

All workflows reflect real banking systems

Suitable for:

MCA Project Submission

Project Viva / Demonstration

🚀 Future Enhancements

Account statement scheduling

Advanced fraud analytics

Multi-factor authentication (OTP)

Graphical financial analytics

REST API exposure

Mobile application support

👨‍🎓 Author

MCA Final Year Project
Core Banking Management System (CBMS)

📄 License

This project is developed for academic purposes only.


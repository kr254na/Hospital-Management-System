# 🏥 City General Hospital Management System

A high-performance enterprise console application built to simulate real-world hospital operations including patient care, staff workflows, billing automation, and audit tracking.

Designed using Java 17, layered architecture, and Spring Boot design principles, the system demonstrates strong backend engineering skills, concurrency handling, and production-grade RBAC security.

---

# ✨ Why This Project Stands Out

✔ Enterprise-style layered architecture  
✔ Thread-safe concurrent reporting system  
✔ Production-grade RBAC implementation  
✔ Fully automated billing engine  
✔ Strong focus on validation & data integrity  
✔ Clean modular code ready for scaling  

---

# 🧠 Core Features

## 🔐 Secure Login Gateway

- Role-based login sessions  
- Authentication validation layer  
- Session ownership tracking  

---

## 🛡 Role-Based Access Control (RBAC)

Supported roles:

- ADMIN  
- DOCTOR  
- NURSE  
- RECEPTIONIST  

Each role has strictly isolated permissions ensuring data privacy & operational safety.

---

## ⚡ Asynchronous Audit Engine

Uses ExecutorService thread pools to:

- Generate audit logs in background  
- Prevent UI blocking  
- Improve responsiveness  
- Enable high throughput reporting  

---

## 💳 Smart Billing System

Auto-calculates at discharge:

- Consultation fees  
- Room stay duration charges  
- Pharmacy purchases  
- Final invoice generation  

---

## 🧵 Thread-Safe Backend Design

Built to support concurrent operations via:

- Centralized controller orchestration  
- Safe DB access patterns  
- Transaction-aware service layer  
- Graceful shutdown handling  

---

# 🛠 Tech Stack

| Layer | Technology |
|------|------------|
| Language | Java 17+ |
| Architecture | Spring Boot Design Principles |
| Database | MySQL via JDBC |
| Concurrency | ExecutorService API |
| IDE | IntelliJ IDEA / Standard JDK |
| Logging | SLF4J (optional) |
| Pooling | HikariCP (optional) |

---

# 📂 Project Structure

src/
├── config/ # DB configs and system settings

├── controllers/ # UI ↔ Service coordination

├── enums/ # Role & status constants

├── exceptions/ # Custom domain exceptions

├── interfaces/ # Repository + service contracts

├── models/ # POJOs (Patient, User, Bill…)

├── repository/ # JDBC query implementations

├── services/ # Business logic layer

├── sql/ # Schema + seed scripts

└── utility/ # Validators, formatters, helpers


---

# 🔑 Permission Matrix

| Feature | Admin | Doctor | Nurse | Receptionist |
|--------|-------|--------|-------|--------------|
| Infrastructure Mgmt | ✅ | ❌ | ❌ | ❌ |
| Staff Registration | ✅ | ❌ | ❌ | ❌ |
| Patient Admission | ✅ | ✅ | ❌ | ✅ |
| Clinical Records | ✅ | ✅ | ✅ | 👁️ View Only |
| Pharmacy Stock | ✅ | ✅ | ❌ | 👁️ View Only |
| Billing & Finance | ✅ | ❌ | ❌ | ✅ |
| Async Audit | ✅ | ❌ | ❌ | ❌ |

---

# ⚙️ Setup Instructions

## ✅ Prerequisites

- Java 17+  
- Running MySQL server  
- JDBC driver available  

---

## 🗄 Database Setup

Run SQL scripts from:
src/sql/


On first run:



DatabaseSeeder → Creates default Admin account


---

## 📦 Manual Dependency Setup (Non-Maven)

Add these JARs manually if not using build tools:

- MySQL Connector/J  
- HikariCP (optional but recommended)  
- SLF4J (optional logging)  

---

# 🧪 How to Run



Configure DB credentials in config package

Compile project

Run Main class

Login with seeded Admin credentials


---

# 📸 Screenshots

![](https://github.com/user-attachments/assets/b032aa26-33d4-40ee-95f7-3bae9586b2c6)

*Figure 1: Login Screen*
---
![](https://github.com/user-attachments/assets/725e90b9-f952-4868-b3c9-80a7e2b66592)

*Figure 2: Infrastructure Management*
---
![](https://github.com/user-attachments/assets/26c6c3e3-2854-4958-92a9-befbd76dba61)

*Figure 3: Staff Management*
---
![](https://github.com/user-attachments/assets/145b5094-d82c-407e-bbad-b67316341179)

*Figure 4: Patient Management*
---
![](https://github.com/user-attachments/assets/05fb095f-0206-4289-8fbc-0da210c6f02c)

*Figure 5: Clinical Management*
---
![](https://github.com/user-attachments/assets/c39ba1e1-647e-4127-add6-a9c674701f1a)

*Figure 6: Pharmacy Management*
---
![](https://github.com/user-attachments/assets/35cf5351-5e04-4ad5-a324-da7afc78eb91)

*Figure 7: Billing Management*
---
![](https://github.com/user-attachments/assets/c77ed3e5-8dbc-453e-a18e-57ded0e1d58a)

*Figure 8: Bill File*
---
---

# 📊 Engineering Highlights

## ✔ Strong Input Validation
Prevents crashes and enforces data integrity.

## ✔ Graceful Shutdown Hook

Ensures:

- Thread pools close properly  
- No audit corruption  
- Safe resource release  

## ✔ Full Traceability

- Bills timestamped  
- Audits linked to admin  
- Activity fully trackable  

---

# 👨‍💻 Author

**Krishna Agarwal**  
Backend Developer • Spring Boot • Distributed Systems • Concurrency Enthusiast  

---

# ⭐ If You Like This Project

Give it a star ⭐ on GitHub — it helps a lot!

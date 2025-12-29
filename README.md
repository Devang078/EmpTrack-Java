# Employee Accountability Management System (Java Console App)

A self-made, console-based Java application built to manage employee records and task accountability using a cloud-hosted Supabase (PostgreSQL) database.  
This project focuses on backend engineering, structured CRUD workflows, and real-world database integration without relying on any UI frameworks.

---

## 🚀 Project Overview

The **Employee Accountability Management System** is a terminal-driven backend application that allows administrators to manage employees and their assigned tasks efficiently.

The system uses **Java (JDBC)** to communicate directly with **Supabase**, enabling persistent cloud-based storage while maintaining a lightweight and distraction-free console interface.

This project was developed as a **self-learning backend exercise**, emphasizing clean logic, database reliability, and real-world data relationships.

---

## 🧩 Core Capabilities

### 👤 Employee Management
- Add new employees with salary details
- View all employees or fetch by ID
- Update employee name and salary
- Delete employee records safely with confirmation

### 📝 Task Accountability
- Create tasks and assign them to employees
- Track task status (`pending`, etc.)
- View all tasks with employee mapping
- Fetch, update, or delete tasks by ID
- Enforced relationship between employees and tasks

---

## 🛠️ Tech Stack

- **Java** — Core application logic
- **JDBC** — Database connectivity
- **Supabase** — Cloud-hosted PostgreSQL database
- **PostgreSQL** — Relational data storage
- **Console I/O** — Menu-driven user interaction

---

## 🗂️ Database Design

### Employees Table
- `id` (Primary Key)
- `name`
- `salary`
- `created_at`

### Tasks Table
- `id` (Primary Key)
- `title`
- `description`
- `status`
- `employee_id` (Foreign Key)
- `created_at`

Tasks are strictly linked to employees, ensuring accountability and data integrity.

---

## ⚙️ Application Flow

- Interactive console menu with numbered actions
- Input validation and error handling
- Prepared statements to prevent SQL injection
- Timestamp-based record creation
- Real-time Supabase database operations

The system follows a **clear separation of concerns** between:
- User interaction
- Business logic
- Database access

---

## 🎯 Learning Outcomes

This project was built to gain hands-on experience in:
- Java backend development
- JDBC-based cloud database connectivity
- Relational database design
- CRUD operation optimization
- Writing maintainable, modular console applications
- Debugging real-world SQL and connection issues

---

## ▶️ How to Run

1. Clone the repository
2. Add your Supabase database credentials
3. Ensure PostgreSQL JDBC driver is available
4. Run the main Java class
5. Interact with the system through the console menu

---

## 🌱 Project Philosophy

> *Built without frameworks to understand what actually happens under the hood.*

This project reflects a **self-reliant learning approach**, focusing on fundamentals before abstractions and prioritizing backend clarity over UI complexity.

---

## ✨ Author

Self-developed Java backend project for practical learning and real-world database integration.


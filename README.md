# 📖 LiterAlura - Interactive Book Management System

Welcome to **LiterAlura**! This is a backend application developed in Java 25 with Spring Boot that serves as an interactive book catalog via console. The system consumes the public **Gutendex** API (Project Gutenberg), processes the data, and persists it in a relational database (PostgreSQL) for future queries.

## Features

1. **🔍 Search book by title:** Connects to the API and uses the intelligent search algorithm.
2. **📚 List registered books:** Displays all books saved locally.
3. **✍️ List registered authors:** Shows authors and their books.
4. **📅 List authors alive in a specific year:** Historical author filter.
5. **🌐 List books in a specific language:** Filter by language code (EN, PT, etc.).

---

## 🛠️ Technologies Used

* **Java 25 LTS:** Latest and most performant version of the language.
* **Spring Boot 3.4.13:** Updated framework for productivity.
* **Spring Data JPA:** Robust data persistence.
* **PostgreSQL 16:** Modern relational database.
* **Docker & Docker Compose:** Complete environment containerization.
* **JUnit 5 & Mockito:** High-fidelity automated test suite.

---

## 🧪 Robust Automated Tests

The project uses a professional test suite to ensure reliability:

* **Repository Tests (`DataJpaTest`):** Validates complex queries and database integrity (PostgreSQL 16).
* **Service Unit Tests (`Mockito`):** Validates the search algorithm and filters in isolation and quickly.



# 📖 LiterAlura - Interactive Book Management System

Welcome to **LiterAlura**! A modern Java-based book management system that integrates external API searches with local database persistence. Built for developers who value clean architecture and comprehensive testing.

## What Does It Do?

This application lets you search for books through an external API and maintain your own curated collection. Think of it as your personal library management system with the power of cloud-based book discovery.

**Core Capabilities:**
- Search any book title using intelligent API integration
- Maintain a persistent local catalog of your favorite finds
- Explore author collections and their complete works
- Filter books by publication language
- Discover authors who were alive during specific historical periods

## Technical Foundation

**Backend Stack:**
- Java 25 LTS with Spring Boot 3.4.13
- PostgreSQL 16 for data persistence
- Spring Data JPA handling all database operations
- Full Docker containerization for consistent deployment

**Quality Assurance:**
- Comprehensive JUnit 5 test coverage
- Mockito-powered unit tests for business logic isolation
- Integration tests using `@DataJpaTest` for repository layer validation
- Database integrity checks running against actual PostgreSQL instances

## Why This Architecture?

The project demonstrates production-ready patterns: clean separation between API integration and data management, testable service layers, and containerized infrastructure. Whether you're building a portfolio piece or exploring Spring Boot best practices, this codebase showcases modern Java development done right.

---


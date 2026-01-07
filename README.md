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

## Quick Start & Setup

**Prerequisites:**
- Java 25 LTS
- Docker & Docker Compose
- Maven 3.9+

**Getting Started:**
```bash
# Clone and navigate to project
cd literalura

# Build entire environment
./scripts/build.sh

# Start the application
./scripts/start.sh

# Or run everything at once
./scripts/rebuild.sh
```

**Available Scripts:**
- `./scripts/build.sh` - Build Docker images and initialize database
- `./scripts/start.sh` - Start the application with health checks
- `./scripts/test.sh` - Run comprehensive test suite
- `./scripts/logs.sh` - Interactive log viewing
- `./scripts/cleanup.sh` - Clean Docker environment
- `./scripts/rebuild.sh` - Complete environment rebuild
- `./scripts/test-rebuild.sh` - Idempotency testing

## Enhanced Features

**Developer Experience:**
- 🎨 Colored script output with progress indicators
- 🔍 Interactive log viewing with filtering options
- 🧪 Automated idempotency testing for build reliability
- 📊 Comprehensive test coverage with JaCoCo reporting
- 🚀 Multi-environment configuration support

**Production Ready:**
- 🐳 Multi-stage Docker builds with security best practices
- 📝 Structured logging with configurable levels
- 🔧 Environment-specific configurations (dev/docker/test/prod)
- 💾 Database health checks and connection pooling
- 🛡️ Non-root container execution for enhanced security

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
- JaCoCo code coverage reporting with 70% minimum threshold

**Multi-Environment Configuration:**
- **Development**: H2 in-memory database, hot reload enabled
- **Docker**: PostgreSQL with health checks and volume persistence
- **Test**: Isolated test database with clean teardown
- **Production**: Optimized settings with security hardening

## Why This Architecture?

This project demonstrates production-ready patterns: clean separation between API integration and data management, testable service layers, and containerized infrastructure. Whether you're building a portfolio piece or exploring Spring Boot best practices, this codebase showcases modern Java development done right.

**Architecture Highlights:**
- 🏗️ Clean service layer with proper separation of concerns
- 🔄 Custom exception handling with error categorization
- 📦 DTO pattern for external API integration
- 🎯 Repository layer with custom queries and pagination
- ⚡ Optimized Docker builds with layer caching

---


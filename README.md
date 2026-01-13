# 📚 LiteraLura - Simple Book Management System

Welcome to **LiteraLura**! A simple Java application that helps you manage your personal book library.

## What Does It Do?

LiteraLura lets you:
- Add books to your personal library
- Add authors and their information
- Search books by title, language, or author
- Find authors who were alive in specific years
- View all your books and authors

## What You Need

- Java 25 or newer
- Maven 3.9 or newer  
- PostgreSQL database

## How to Set Up

1. **Create the database:**
   ```sql
   CREATE DATABASE literalura;
   CREATE USER postgres WITH PASSWORD 'postgres';
   GRANT ALL PRIVILEGES ON DATABASE literalura TO postgres;
   ```

2. **Run the application:**
   ```bash
   cd Literalura-One
   mvn spring-boot:run
   ```

3. **Use the application:**
   - A menu will appear in your console
   - Type the number of what you want to do
   - Follow the simple prompts

## Project Structure

This is a simple project with just a few files:

```
src/main/java/com/alura/literalura/
├── LiteraLuraApplication.java    # Main entry point
├── LiteraLuraService.java         # All business logic
├── Book.java                      # Book model
├── Author.java                    # Author model
├── BookRepository.java            # Database operations for books
└── AuthorRepository.java          # Database operations for authors
```

## How It Works

1. **LiteraLuraApplication.java** - Starts the Spring Boot application
2. **LiteraLuraService.java** - Shows menus and handles user input
3. **Book.java & Author.java** - Simple data classes
4. **Repository classes** - Spring automatically handles database operations

## Perfect for Learning

This project is designed to be beginner-friendly:
- Simple, clean code with helpful comments
- No complex patterns or over-engineering
- Easy to understand and modify
- Uses Spring Boot but keeps it simple

---

Happy reading with LiteraLura! 📖


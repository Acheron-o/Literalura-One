# 📚 LiteraLura - Simple Book Management System

Welcome to **LiteraLura**! A simple Java application that helps you manage your personal book library.

## What Does It Do?

LiteraLura lets you:
- Add books to your personal library
- Add authors and their information
- Search books from Gutendex API (external book database)
- Search books by title, language, or author
- Find authors who were alive in specific years
- View all your books and authors

## What You Need

- Java 25 or newer
- Maven 3.9 or newer  
- PostgreSQL database
- Internet connection (for API searches)

## How to Set Up

1. **Create the database:**
   ```sql
   CREATE DATABASE literalura;
   CREATE USER postgres WITH PASSWORD 'your_password'; -- (Just an example. Make your own PASSWORD and USER and make sure to remember them)
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
   - Follow simple prompts
   - **Option 8** lets you search books from Gutendex API and save them to your library

## Project Structure

This is a simple project with just a few files:

```
src/main/java/com/alura/literalura/
├── LiteraLuraApplication.java    # Main entry point
├── LiteraLuraService.java         # All business logic
├── Book.java                      # Book model
├── Author.java                    # Author model
├── BookRepository.java            # Database operations for books
├── AuthorRepository.java          # Database operations for authors
├── GutendexService.java           # API integration service
├── GutendexBook.java             # API book DTO
└── GutendexResponse.java         # API response wrapper
```

## How It Works

1. **LiteraLuraApplication.java** - Starts the Spring Boot application
2. **LiteraLuraService.java** - Shows menus and handles user input
3. **Book.java & Author.java** - Simple data classes
4. **Repository classes** - Spring automatically handles database operations
5. **GutendexService.java** - Connects to external Gutendex API to search books
6. **DTO classes** - Handle API response format

## API Integration

The application uses **Gutendex API** (https://gutendex.com) to search for books:
- Search by title, author, or language
- Convert API results to local database entities
- Save interesting books to your personal library
---

Happy reading with LiteraLura! 📖


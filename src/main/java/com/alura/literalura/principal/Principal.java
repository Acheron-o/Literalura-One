package com.alura.literalura.principal;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Livro;
import com.alura.literalura.model.dto.LivroDTO;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LivroRepository;
import com.alura.literalura.service.GutendexService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private final Scanner scanner = new Scanner(System.in);
    private final GutendexService gutendexService = new GutendexService();

    private final LivroRepository livroRepository;
    private final AutorRepository autorRepository;

    public Principal(LivroRepository livroRepository, AutorRepository autorRepository) {
        this.livroRepository = livroRepository;
        this.autorRepository = autorRepository;
    }

    public void displayMenu() {
        int option = -1;
        while (option != 0) {
            String menu = """
                    
                    --------------------------------
                    Choose the number of your option:
                    --------------------------------
                    1 - Search book by title
                    2 - List registered books
                    3 - List registered authors
                    4 - List authors alive in a specific year
                    5 - List books in a specific language
                    
                    0 - Exit                                 
                    """;

            System.out.println(menu);
            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine();

                switch (option) {
                    case 1 -> searchBookOnline();
                    case 2 -> listRegisteredBooks();
                    case 3 -> listRegisteredAuthors();
                    case 4 -> listAuthorsAlive();
                    case 5 -> listBooksByLanguage();
                    case 0 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid option");
                }
            } else {
                System.out.println("Please enter a number.");
                scanner.next();
            }
        }
    }

    private void searchBookOnline() {
        System.out.println("Enter the book title to search:");
        String bookTitle = scanner.nextLine();

        Optional<LivroDTO> bookResult = gutendexService.searchBook(bookTitle);

        if (bookResult.isPresent()) {
            LivroDTO livroDTO = bookResult.get();
            Livro livro = new Livro(livroDTO);
            
            if (!livroDTO.authors().isEmpty()) {
                var autorDTO = livroDTO.authors().get(0);
                Optional<Autor> existingAuthor = autorRepository.findByName(autorDTO.name());
                
                if (existingAuthor.isPresent()) {
                    livro.setAuthor(existingAuthor.get());
                } else {
                    Autor newAuthor = new Autor(autorDTO);
                    autorRepository.save(newAuthor);
                    livro.setAuthor(newAuthor);
                }
            }

            try {
                livroRepository.save(livro);
                System.out.println(livro);
            } catch (Exception e) {
                System.out.println("Could not save the book. It may already exist in the database.");
            }
            
        } else {
            System.out.println("Book not found.");
        }
    }

    private void listRegisteredBooks() {
        List<Livro> livros = livroRepository.findAll();
        livros.stream()
                .sorted(Comparator.comparing(Livro::getTitle))
                .forEach(System.out::println);
    }

    private void listRegisteredAuthors() {
        List<Autor> autores = autorRepository.findAll();
        autores.stream()
                .sorted(Comparator.comparing(Autor::getName))
                .forEach(a -> {
                    System.out.println("Author: " + a.getName());
                    System.out.println("Birth year: " + a.getBirthYear());
                    System.out.println("Death year: " + a.getDeathYear());
                    List<String> bookList = a.getBooks().stream()
                            .map(Livro::getTitle)
                            .collect(Collectors.toList());
                    System.out.println("Books: " + bookList + "\n");
                });
    }

    private void listAuthorsAlive() {
        System.out.println("Enter the year you want to search:");
        if (scanner.hasNextInt()) {
            int year = scanner.nextInt();
            scanner.nextLine();

            List<Autor> autores = autorRepository.findAuthorsAliveInYear(year);
            if (autores.isEmpty()) {
                System.out.println("No authors found alive in this year.");
            } else {
                autores.forEach(System.out::println);
            }
        } else {
            System.out.println("Invalid year.");
            scanner.next();
        }
    }

    private void listBooksByLanguage() {
        String languageMenu = """
                Enter the language code to search:
                es - Spanish
                en - English
                fr - French
                pt - Portuguese
                """;
        System.out.println(languageMenu);
        String language = scanner.nextLine();

        List<Livro> livros = livroRepository.findByLanguage(language);
        if (livros.isEmpty()) {
            System.out.println("No books found in this language in the database.");
        } else {
            livros.forEach(System.out::println);
        }
    }
}
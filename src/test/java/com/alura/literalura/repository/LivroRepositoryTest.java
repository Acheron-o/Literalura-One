package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Livro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LivroRepositoryTest {

    @Autowired
    private LivroRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Should find books by language correctly")
    void findByLanguage() {
        Autor author = new Autor();
        author.setName("Test Author");
        em.persist(author);

        Livro bookEn = new Livro();
        bookEn.setTitle("Book One");
        bookEn.setLanguage("en");
        bookEn.setAuthor(author);
        bookEn.setDownloadCount(100);
        em.persist(bookEn);

        Livro bookPt = new Livro();
        bookPt.setTitle("Book Two");
        bookPt.setLanguage("pt");
        bookPt.setAuthor(author);
        bookPt.setDownloadCount(200);
        em.persist(bookPt);

        var results = repository.findByLanguage("en");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Book One");
    }

    @Test
    @DisplayName("Should save book successfully and persist relationship")
    void bookPersistence() {
        Autor author = new Autor();
        author.setName("George Orwell");
        em.persist(author);

        Livro book = new Livro();
        book.setTitle("1984");
        book.setLanguage("en");
        book.setAuthor(author);
        book.setDownloadCount(5000);
        
        Livro savedBook = repository.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getAuthor()).isEqualTo(author);
    }
}

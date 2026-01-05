package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AutorRepositoryTest {

    @Autowired
    private AutorRepository repository;

    @Autowired
    private TestEntityManager em;

    @Test
    @DisplayName("Should return author who was alive in the searched year")
    void findAuthorsAliveScenario1() {
        Autor author = new Autor();
        author.setName("Machado de Assis");
        author.setBirthYear(1839);
        author.setDeathYear(1908);
        em.persist(author);

        var authors = repository.findAuthorsAliveInYear(1900);

        assertThat(authors).isNotEmpty();
        assertThat(authors.get(0).getName()).isEqualTo("Machado de Assis");
    }

    @Test
    @DisplayName("Should not return author who was born after the searched year")
    void findAuthorsAliveScenario2() {
        Autor author = new Autor();
        author.setName("J.K. Rowling");
        author.setBirthYear(1965);
        em.persist(author);

        var authors = repository.findAuthorsAliveInYear(1900);

        assertThat(authors).isEmpty();
    }

    @Test
    @DisplayName("Should not return author who died before the searched year")
    void findAuthorsAliveScenario3() {
        Autor author = new Autor();
        author.setName("Jane Austen");
        author.setBirthYear(1775);
        author.setDeathYear(1817);
        em.persist(author);

        var authors = repository.findAuthorsAliveInYear(1900);

        assertThat(authors).isEmpty();
    }
}

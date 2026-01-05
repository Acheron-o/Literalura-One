package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    Optional<Autor> findByName(String name);

    @Query("SELECT a FROM Autor a WHERE a.birthYear <= :year AND (a.deathYear >= :year OR a.deathYear IS NULL)")
    List<Autor> findAuthorsAliveInYear(int year);
}

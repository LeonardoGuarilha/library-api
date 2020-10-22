package com.projeto.library.libraryapi.repository;

import com.projeto.library.libraryapi.api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    // Não preciso criar a implementação desse método, o spring faz isso em runtime
    boolean existsByIsbn(String isbn);
}

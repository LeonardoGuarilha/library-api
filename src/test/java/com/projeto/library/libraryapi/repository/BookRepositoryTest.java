package com.projeto.library.libraryapi.repository;

// TESTES de integração

import com.projeto.library.libraryapi.api.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// Fazer testes com o jpa. Cria uma instancia do banco de dados em memoria para executar os testes e, quando
// finalizar os testes, ele apaga tudo
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("It should return true when find a book in the database with isbn")
    public void returnTrueWhenIsbnExists(){
        // cenário
        String isbn = "123";
        // Crio um livro
        Book book = Book.builder().title("As aventuras").author("Leonardo").isbn(isbn).build();
        entityManager.persist(book);

        // execução
        boolean isbnExists = bookRepository.existsByIsbn(isbn);

        // verificação
        Assertions.assertThat(isbnExists).isTrue();
    }

    @Test
    @DisplayName("It should return false when not finding a book in the database with isbn")
    public void returnFalseWhenIsbnDoesNotExists(){
        // cenário
        String isbn = "123";

        // execução
        boolean isbnExists = bookRepository.existsByIsbn(isbn);

        // verificação
        Assertions.assertThat(isbnExists).isFalse();
    }
}

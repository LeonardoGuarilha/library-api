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

import java.util.Optional;

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
        Book book = Book.builder().title("Aventuras").author("Leonardo").isbn(isbn).build();
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

    @Test
    @DisplayName("it should be able to return a book with an id")
    public void getByIdTest(){
        // cenário
        Book book = createBook();
        entityManager.persist(book);

        // execução
        Optional<Book> foundBook = bookRepository.findById(book.getId());

        // verificação
        Assertions.assertThat(foundBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("it sould be able to save a book")
    public void saveBook(){
        // cenário
        Book book = createBook();

        // execução
        Book savedBook = bookRepository.save(book);

        // verificação
        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("it sould be able to delete a book")
    public void deleteBookTest(){
        // cenário
        Book book = createBook();
        entityManager.persist(book);

        // execução
        Book foundBook = entityManager.find(Book.class, book.getId());
        bookRepository.delete(foundBook);
        Book deletedBook = entityManager.find(Book.class, book.getId());

        // verificação
        Assertions.assertThat(deletedBook).isNull();
    }

    private Book createBook() {
        return Book.builder().title("Aventuras").author("Leonardo").isbn("123").build();
    }
}

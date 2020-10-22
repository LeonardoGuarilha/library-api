package com.projeto.library.libraryapi.service;

import com.projeto.library.libraryapi.api.entity.Book;
import com.projeto.library.libraryapi.api.exceptions.BusinessExeption;
import com.projeto.library.libraryapi.repository.BookRepository;
import com.projeto.library.libraryapi.service.imp.BookServiceImp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

// Para usar somente o assertThat como método estático
//import static org.assertj.core.api.Assertions.assertThat;

// Apenas testes unitários

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService bookService;
    // O spring já tem uma implementação padrão para todos os repositories, por isso eu coloco o MockBean
    @MockBean
    private BookRepository bookRepository;

    @BeforeEach
    public void setUp(){
        this.bookService = new BookServiceImp(bookRepository);
    }

    @Test
    @DisplayName("It should be able to save a new book")
    public void saveBookTest(){
        // cenário
        // Uso o lombok para criar a instancia
        Book book = createBook();
        // Estou lidando com mock, então eu sempre tenho que simular o retorno do mock
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(bookRepository.save(book))
                .thenReturn(Book.builder().id(1l).title("Title").author("Autor").isbn("123").build());

        // execução
        Book savedBook = bookService.save(book);

        // verificação
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getTitle()).isEqualTo("Title");
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo("Autor");
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo("123");
    }

    @Test
    @DisplayName("It should not be able to create a book with duplicated ISBN")
    public void createBookWithDuplicatedISBN() {
        // Cenário
        Book book = createBook();
        // Estou lidando com mock, então eu sempre tenho que simular o retorno do mock
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        // Execução
        // Quando eu executar o save, ele vai lançar o erro
         Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));

         // Verificação
         Assertions.assertThat(exception)
                 .isInstanceOf(BusinessExeption.class)
                 .hasMessage("Isbn já cadastrado");

         // Verificar que o método save() do repositório não foi chamado
        Mockito.verify(bookRepository, Mockito.never()).save(book);

    }

    private Book createBook() {
        return Book.builder().id(1l).title("Title").author("Autor").isbn("123").build();
    }
}

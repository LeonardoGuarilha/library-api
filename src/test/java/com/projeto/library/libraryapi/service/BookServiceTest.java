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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("it should be able to return a book with an id")
    public void getByIdTest(){
        // cenário
        Long id = 1l;

        Book book = createBook();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        // execução
        Optional<Book> foundBook = bookService.getById(id);

        // Verificações
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    @DisplayName("it should not return a book by id if the id is does not exists")
    public void doesNotgetByIdTest(){
        // cenário
        Long id = 1l;

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // execução
        Optional<Book> book = bookService.getById(id);

        // Verificações
        Assertions.assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("it should be able to delete a book")
    public void deleteBook(){
        // cenário
        Book book = createBook();

        // execução
        // Vai verificar que não foi lançado nenhuma exceção
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(book));

        // verificação
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("it should return exception if the book does not exist")
    public void deleteInvalidBook(){
        // cenário
        // cira um livro vazio
        Book book = new Book();

        // execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.delete(book));

        // verificação
        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("it should return exception if the book does not exist for update")
    public void updateInvalidBook(){
        // cenário
        // cira um livro vazio
        Book book = new Book();

        // execução
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        // verificação
        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("it should be able to update a book")
    public void updateBook(){
        // cenário
        Long id = 1l;

        // Livro a atualizar
        Book updatingBook = Book.builder().id(id).build();

        // Simulação de atualização
        Book updatedBook = createBook();
        updatedBook.setId(id);
        Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        // execução
        Book book = bookService.update(updatingBook);

        // verificação
        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());

    }

    @Test
    @DisplayName("it should filter books")
    public void findBooksByFilterTest(){
        // cenário
        Book book = createBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> list = Arrays.asList(book);
        // PageImpl herda de Page
        Page<Book> page = new PageImpl<Book>(list, pageRequest, 1);
        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // execução
        Page<Book> result = bookService.find(book, pageRequest);

        // verificação
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(list);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Book createBook() {
        return Book.builder().id(1l).title("Title").author("Autor").isbn("123").build();
    }
}

package com.projeto.library.libraryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.library.libraryapi.api.dto.BookDTO;
import com.projeto.library.libraryapi.api.entity.Book;
import com.projeto.library.libraryapi.api.exceptions.BusinessExeption;
import com.projeto.library.libraryapi.service.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.Optional;


//TESTA O BookController

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
// Anotations para configurar o teste para a rest api
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    // Definir a rota
    private static String BOOK_API = "/api/books";

    // Injeta um mock para simular uma requisição para a api
    @Autowired
    MockMvc mvc;

    // Crio um mock
    // O MockBean é um mock do spring especializado para criar essa instancia mockada e colocar no meu contexto
    // de injeção de dependência
    @MockBean
    BookService bookService;

    @Test
    @DisplayName("It should be able to create a new book")
    public void createBootTest() throws Exception {

        // Crio o dto que é mandado na requisição
        BookDTO bookDTO = createNewBook();

        // Cria um livo, para simular o retorno do service
        Book savedBook = Book.builder().id(1l).author("Leonardo").title("As aventuras").isbn("001").build();

        // Quando eu chamo o save() no controller ele retorna esse livro salvo, o savedBook
        // Para simular o comportamento do service save
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO); // Recebe um objeto de qualquer tipo e transforma em string

        // Ciar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);// Corpo da requisição

        // Faço a requisição
        mvc
           .perform(request) // Faço a requisição
           .andExpect(MockMvcResultMatchers.status().isCreated()) // espero que o retorno da requisição seja created
           .andExpect(MockMvcResultMatchers.jsonPath("id").value(1l)) // Espero que tenha um id no json de retorno
           .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle())) // Espero no json de respota o title seja a que está no json criado acima.
           .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor())) // Espero que no json de resposta o autor seja a que está no json criado acima.
           .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn())); // Espero no json de resposta que o isbn seja a que está no json criado acima.
    }


    @Test
    @DisplayName("It should throw a new exception if there is not enough data")
    public void createInvalidBootTest() throws Exception {
        // Recebe um objeto de qualquer tipo e transforma em string
        // Nesse caso um DTO sem informações
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        // Ciar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);// Corpo da requisição

        mvc
           .perform(request)
           .andExpect(MockMvcResultMatchers.status().isBadRequest())
           .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3))); // Será uma mensagem de erro para cada propriedade e, tem 3 propriedades(title, authot e isbn)
    }

    @Test
    @DisplayName("It should not be able to create a book with duplicated ISBN")
    public void createBookWithDuplicatedISBN() throws Exception {

        BookDTO bookDTO = createNewBook();

        // Recebe um objeto de qualquer tipo e transforma em string
        // Nesse caso um DTO sem informações
        String json = new ObjectMapper().writeValueAsString(bookDTO);
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessExeption("Isbn já cadastrado"));

        // Ciar uma requisição
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);// Corpo da requisição

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Isbn já cadastrado"));
    }

    @Test
    @DisplayName("it should return a book detail with given id")
    public void getBookDetailTest() throws Exception {
        // cenário (given)
        Long id = 1l;

        Book book = Book.builder()
                        .id(id)
                        .title(createNewBook().getTitle())
                        .author(createNewBook().getAuthor())
                        .isbn(createNewBook().getIsbn())
                        .build();

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        // execução (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        // Verificação
        // Fazer a requisição
        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));


    }

    @Test
    @DisplayName("it should not return a book that does not exists")
    public void bookNotFoundTest() throws Exception {

        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("it should be able to delete a book")
    public void deleteBookTest() throws Exception {

        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc
              .perform(request)
              .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("it should return not found when the book is not found")
    public void notFoundBookTest() throws Exception {

        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("it should be able to update a book")
    public void updateBookTest() throws Exception{
        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());

        Book updatingBook = Book.builder().id(1l).title("some title").author("some author").isbn("321").build();
        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(updatingBook));

        Book updatedBook = Book.builder().id(id).author("Leonardo").title("As aventuras").isbn("001").build();
        BDDMockito.given(bookService.update(updatingBook)).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("it should return not found if the book is not found")
    public void updateInexistentBookTest() throws Exception{

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(bookService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("it should filter books")
    public void findBooksByFilterTest() throws Exception {
        Long id = 1l;

        Book book = Book.builder()
                    .id(id)
                    .title(createNewBook().getTitle())
                    .author(createNewBook().getAuthor())
                    .isbn(createNewBook().getIsbn())
                    .build();
        // Pageable, para fazer buscas paginadas, passo qual a página e quantos registros ele deve trazer
        BDDMockito.given(bookService.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                  .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,100), 1));

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                              book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
            .perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
            .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Leonardo").title("As aventuras").isbn("001").build();
    }
}

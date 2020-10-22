package com.projeto.library.libraryapi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


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

    private BookDTO createNewBook() {
        return BookDTO.builder().author("Leonardo").title("As aventuras").isbn("001").build();
    }
}

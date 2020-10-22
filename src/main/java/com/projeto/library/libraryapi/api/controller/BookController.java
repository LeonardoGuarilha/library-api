package com.projeto.library.libraryapi.api.controller;

import com.projeto.library.libraryapi.api.dto.BookDTO;
import com.projeto.library.libraryapi.api.entity.Book;
import com.projeto.library.libraryapi.api.exceptions.ApiErrors;
import com.projeto.library.libraryapi.api.exceptions.BusinessExeption;
import com.projeto.library.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    // Injeção de dependencia
    private BookService bookService;
    private ModelMapper modelMapper;

    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    // O @Valid vai fazer com que o Spring MVC valide esse objeto com as validações que tem lá no BookDTO
    public BookDTO createBook(@RequestBody @Valid BookDTO bookDTO){
        // Faz a conversão de um DTO para um book
        // Sem o ModelMapper, somente com o builder() do lombok
        // Book entity = Book.builder().author(bookDTO.getAuthor()).title(bookDTO.getTitle()).isbn(bookDTO.getIsbn()).build();

        // Converto o BookDTO para Book
        // Com o ModelMapper
        // Já trasnfere todas as propriedades de mesmo nome para a instancia do Book
        Book entity = modelMapper.map(bookDTO, Book.class);


        entity = bookService.save(entity);

        // Return com o builder convertendo de Book para BookDTO
//        return BookDTO.builder()
//                .id(entity.getId())
//                .author(entity.getAuthor())
//                .title(entity.getTitle())
//                .isbn(entity.getIsbn())
//                .build();
        // Mesmo result só que com o ModelMapper
        return modelMapper.map(entity, BookDTO.class);
    }

    // Except handler
    // MethodArgumentNotValidException: lançado toda vez que tentamos validar um objeto( @Valid do createBook)
    // e o objeto não está valido
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExeptions(MethodArgumentNotValidException methodArgumentNotValidException){
        // getBindingResult, resultado da validação que ocorreu ao tentar validar o objeto com o @Valid
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();

        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessExeption.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessExeption businessExeption) {
        return new ApiErrors(businessExeption);
    }
}

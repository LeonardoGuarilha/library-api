package com.projeto.library.libraryapi.api.controller;

import com.projeto.library.libraryapi.api.dto.BookDTO;
import com.projeto.library.libraryapi.api.entity.Book;
import com.projeto.library.libraryapi.api.exceptions.ApiErrors;
import com.projeto.library.libraryapi.api.exceptions.BusinessExeption;
import com.projeto.library.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id){
        return bookService
                .getById(id)
                .map(book -> modelMapper.map(book, BookDTO.class)) // Se encontrar o livro, ele vai ser mapeado para o BookDTO
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); // Se o livro não for encontrado

    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id){
        // Se não achar o livro, nvai ser lançado a exeption not found
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book);
    }

    @PutMapping("{id}")
    // Por default retorna status 200
    public BookDTO updateBook(@PathVariable Long id, BookDTO bookDTO){
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(bookDTO.getAuthor());
        book.setTitle(bookDTO.getTitle());
        book = bookService.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    // Quando for passado na query params as propriedades, o spring já encaixa com o nome do DTO
    // A mesma coisa com o pageable
    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable) {
        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book> result = bookService.find(filter, pageable);
        List<BookDTO> list = result.getContent()
                .stream() // a stream() serve para fazermos algumas operações em cima de coleções
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(list, pageable, result.getTotalElements());
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

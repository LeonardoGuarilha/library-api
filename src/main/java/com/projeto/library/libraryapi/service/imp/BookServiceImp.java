package com.projeto.library.libraryapi.service.imp;

import com.projeto.library.libraryapi.api.entity.Book;
import com.projeto.library.libraryapi.api.exceptions.BusinessExeption;
import com.projeto.library.libraryapi.repository.BookRepository;
import com.projeto.library.libraryapi.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Regra de negócio

@Service
public class BookServiceImp implements BookService {

    private BookRepository bookRepository;

    public BookServiceImp(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {

        if(bookRepository.existsByIsbn(book.getIsbn())){
            throw new BusinessExeption("Isbn já cadastrado");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return this.bookRepository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Book id can not be null");
        }

        this.bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if(book == null || book.getId() == null){
            throw new IllegalArgumentException("Book id can not be null");
        }

        // O save tanto salva quanto atualiza
        return this.bookRepository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withIgnoreNullValues()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
        );
        return bookRepository.findAll(example, pageRequest);
    }
}

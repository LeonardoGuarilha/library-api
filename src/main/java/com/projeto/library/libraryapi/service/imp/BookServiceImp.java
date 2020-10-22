package com.projeto.library.libraryapi.service.imp;

import com.projeto.library.libraryapi.api.entity.Book;
import com.projeto.library.libraryapi.api.exceptions.BusinessExeption;
import com.projeto.library.libraryapi.repository.BookRepository;
import com.projeto.library.libraryapi.service.BookService;
import org.springframework.stereotype.Service;

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
}

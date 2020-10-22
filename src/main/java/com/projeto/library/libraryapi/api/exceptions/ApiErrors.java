package com.projeto.library.libraryapi.api.exceptions;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiErrors {
    private List<String> errors;

    public ApiErrors(BindingResult bindingResult){
        // extraio os erros do bindResult e coloco na lista
        this.errors = new ArrayList<>();
        // o forEach vai iterar sobre os erros que estão em bindingResult.getAllErrors()
        // getDefaultMessage, retorna a mensagem de erro associada ao resultado da validação
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()) );
    }

    public ApiErrors(BusinessExeption businessExeption) {
        this.errors = Arrays.asList(businessExeption.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}

package com.projeto.library.libraryapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder // gera um builder para a classe com as propriedades, o Builder adiciona um construtor com todas as propriedades
@NoArgsConstructor // Para remover as propriedades do construtor que foram colocadas pelo @Builder
@AllArgsConstructor
public class BookDTO {
    private Long id;

    @NotEmpty // Validação
    private String title;

    @NotEmpty
    private String author;

    @NotEmpty
    private String isbn;
}

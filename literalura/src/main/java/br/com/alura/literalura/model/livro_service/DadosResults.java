package br.com.alura.literalura.model.livro_service;

import br.com.alura.literalura.model.livro.DadosLivro;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosResults(
    @JsonAlias("results")
    List<DadosLivro> results){
        public void add(DadosResults dadosResults) {
        }
}

package br.com.alura.literalura.model.livro;

import br.com.alura.literalura.model.autor.DadosAutor;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosLivro(@JsonAlias({"title"}) String titulo,
                         @JsonAlias({"authors"}) List<DadosAutor> autores,
                         @JsonAlias("languages") List<String> idiomas,
                         @JsonAlias("download_count") Double numeroDownloads) {
}

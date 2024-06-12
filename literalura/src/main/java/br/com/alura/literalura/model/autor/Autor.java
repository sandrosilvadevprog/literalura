package br.com.alura.literalura.model.autor;

import br.com.alura.literalura.model.livro.Livro;
import jakarta.persistence.*;

import java.util.List;
@Entity
@Table(name = "autores")
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer anoDeNascimento;
    private Integer anoDeFalecimento;
    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // Mapeamento um para muitos
    private List<Livro> livros;

    public Autor(){
    }
    public Autor(Autor autor){

    }


    public Autor(String nome, Integer anoDeNascimento, Integer anoDeFalecimento){
        this.nome = nome;
        this.anoDeNascimento = anoDeNascimento;
        this.anoDeFalecimento = anoDeFalecimento;
        this.livros = livros;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAnoDeNascimento() {
        return anoDeNascimento;
    }

    public void setAnoDeNascimento(Integer anoDeNascimento) {

        this.anoDeNascimento = anoDeNascimento;
    }

    public Integer getAnoDeFalecimento() {

        return anoDeFalecimento;
    }

    public void setAnoDeFalecimento(Integer anoDeFalecimento) {

        this.anoDeFalecimento = anoDeFalecimento;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivro(List<Livro> livros) {
        this.livros = livros;
    }

    @Override
    public String toString() {
        return "nome='" + nome + '\'' +
                ", dataDeNascimento=" + anoDeNascimento +
                ", dataDeFalecimento=" + anoDeFalecimento;
    }
}

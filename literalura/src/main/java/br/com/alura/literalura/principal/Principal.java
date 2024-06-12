package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.autor.Autor;
import br.com.alura.literalura.model.livro_service.DadosResults;
import br.com.alura.literalura.model.livro_service.Idiomas;
import br.com.alura.literalura.model.livro.Livro;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;

import java.util.*;

public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String BASE_URL = "https://gutendex.com/books/?search=";
    private LivroRepository repositorio;
    private AutorRepository autorRepository;
    private String nomeDoLivro;

    private AutorRepository livroRepository;


    public Principal(AutorRepository autorRepository, LivroRepository repositorio) {
        this.repositorio = repositorio;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """       
                    00 - Sair                
                    01 - Buscar livro na web
                    02 - Lista livros armazenados
                    03 - Listar autores armazenados 
                    04 - Listar autores vivos em um determinado ano 
                    05 - Listar livros em um determinado idioma  
                    06 - Listar os três livros mais baixados    
                    07 - Listar autor pelo nome                  
                                      
                    """;

            System.out.println("\n\n" + menu);
            System.out.println("Opção: ");
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    obterDadosLivro();
                    break;
                case 2:
                    listarLivrosArmazenados();
                    break;
                case 3:
                    listarAutoresArmazenados();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 6:
                    listarTop3Downloads();
                    break;
                case 7:
                    listarAutorPeloNome();
                    break;
                case 0:
                    System.out.println("\nEncerrando sistema...");
                    break;
                default:
                    System.out.println("\nOpçao inválida!");
            }
        }
    }


    private String solicitarDados() {
        System.out.println("Digite o nome do livro para a busca");
        nomeDoLivro = leitura.nextLine();
        return nomeDoLivro;
    }

    private DadosResults buscarDadosAPI(String nomeDoLivro) {
        var json = consumoApi.obterDados(BASE_URL + nomeDoLivro.replace(" ", "+"));
        var dados = conversor.obterDados(json, DadosResults.class);
        System.out.println("\n" + dados);
        return dados;
    }


    private Optional<Livro> obterInfoLivro(DadosResults dadosLivro, String nomeLivro) {
        Optional<Livro> livros = dadosLivro.results().stream()
                .filter(l -> l.titulo()
                        .toLowerCase()
                        .contains(nomeLivro.toLowerCase()))
                .map(b -> new Livro(b.titulo(),
                        b.idiomas(),
                        b.numeroDownloads(),
                        b.autores()))
                .findFirst();
        return livros;
    }


    private Optional<Livro> obterDadosLivro() {
        String tituloLivro = solicitarDados();

        DadosResults infoLivro = buscarDadosAPI(tituloLivro);

        Optional<Livro> livro = obterInfoLivro(infoLivro, tituloLivro);


        if (livro.isPresent()) {
            Livro l = livro.get();
            Autor autor = l.getAutor();


            Optional<Autor> autorExistente = autorRepository.findByNome(autor.getNome());

            if (autorExistente.isEmpty()) {
                // Salva o autor antes de salvar o livro
                autorRepository.save(autor);
            } else {
                // Atualiza o autor do livro com o autor existente
                l.setAutor(autorExistente.get());
            }


            repositorio.save(l);

            System.out.println(l);

            System.out.println("Livro salvo com sucesso!");
        } else {
            System.out.println("\nLivro não encontrado!\n");
        }
        return livro;
    }


    public void exibeDadosLivros(Livro livro) {
        var exibeLivro = "\n---------------- LIVRO ----------------" +
                "\nTitulo: " + livro.getTitulo() +
                "\nAutor: " + livro.getAutor().getNome() +
                "\nIdioma: " + livro.getIdiomas() +
                "\nNumero de downloads: " + livro.getNumeroDownloads() +
                "\n--------------------------------------\n";
        System.out.println(exibeLivro);
    }


    private void listarLivrosArmazenados() {
        List<Livro> listaLivro = repositorio.findAll();
        listaLivro.forEach(this::exibeDadosLivros);
    }


    public void exibeDadosAutores(Autor autor) {
        var exibeAutor = "\n---------------- AUTOR ----------------" +
                "\nNome: " + autor.getNome() +
                "\nNascido em: " + autor.getAnoDeNascimento()+
                "\nFalecido em: " + autor.getAnoDeFalecimento() +
                "\n--------------------------------------\n";
        System.out.println(exibeAutor);
    }


    private void listarAutoresArmazenados() {
        List<Autor> listaAutores = autorRepository.findAll();

        if (listaAutores.isEmpty()){
            System.out.println("\nNão há autor(es) armazenado(os)!");
        } else {
            System.out.println("\nAutor(es) encontrado(os):");

            listaAutores.stream()
                    .sorted(Comparator.comparing(Autor::getNome));
            listaAutores.forEach(this::exibeDadosAutores);
        }
    }


    private int solicitarAno() {
        System.out.println("Digite o ano para o qual deseja saber um autor vivo:");

        while (true) {
            try {
                int ano = leitura.nextInt();
                leitura.nextLine();
                return ano;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, digite um número inteiro.");
                leitura.nextLine();
            }
        }
    }


    private void listarAutoresVivos() {
        int ano = solicitarAno();

        if (ano < 0) {
            System.out.println("Ano inválido!");
            return;
        }

        List<Autor> autoresVivosEmAno = repositorio.obterAutoresVivosEmAno(ano);

        System.out.println("\n---------------- Autor(es) encontrado(os) para o período informado: ----------------");

        autoresVivosEmAno.stream()
                .sorted(Comparator.comparing(Autor::getNome))
                .forEach(this::exibirAutor);
        if (autoresVivosEmAno.isEmpty()) {
            System.out.println("\nNão foi encontrado nenhum registro para o ano informado!");
        }
    }


    private void exibirAutor(Autor autor) {
        System.out.printf("\nAutor: %s Nascido: %s - Falecido: %s",
                autor.getNome(),
                autor.getAnoDeNascimento(),
                autor.getAnoDeFalecimento());
    }


    private void listarLivrosPorIdioma() {
        String idiomasList = """
                Escolha o idioma do livro que deseja buscar                
                en - Inglês
                es - Espanhol
                fr - Francês                
                pt - Português
                                
                """;
        System.out.println(idiomasList);
        System.out.println("Opção: ");
        String text = leitura.nextLine();

        var idoma = Idiomas.fromString(text);

        List<Livro> livroIdioma = repositorio.findByIdiomas(idoma);

        System.out.println("\n****** Livro(os) encontrado(os) para o idioma (" + idoma + ") ******");

        livroIdioma.forEach(this::exibeDadosLivros);
    }


    private void listarTop3Downloads() {
        List<Livro> livros = repositorio.findAll();

        System.out.println("\n---------------- Os três livros mais baixados são: ---------------- ");

        livros.stream()
                .sorted(Comparator.comparingDouble(Livro::getNumeroDownloads).reversed())
                .limit(3)
                .forEach(l -> System.out.printf("\nTítulo: %s - Downloads: %.0f", l.getTitulo(), l.getNumeroDownloads()));
    }


    private void exibeDadosLivroAutor(Livro livro) {
        System.out.printf("Livro: %s\n", livro.getTitulo());
    }


    private void listarAutorPeloNome() {
        System.out.println("Digite um trecho do nome do autor que deseja buscar");
        var trechoNomeAutor = leitura.nextLine();

        List<Autor> autorEncontrado = autorRepository.autorPorTrechoDoNome(trechoNomeAutor);

        if (autorEncontrado.isEmpty()) {
            System.out.println("\nNão foi localizado nenhum autor com esse nome!");
        } else {
            autorEncontrado.forEach(a ->
                    System.out.printf("\nAutor encontrado: %s\n",
                            a.getNome()));

            System.out.println("---------------- LIVROS ENCONTRADOS ---------------- ");

            for (Autor autor : autorEncontrado) {
                List<Livro> livrosDoAutor = autor.getLivros();

                if (!livrosDoAutor.isEmpty()) {
                    // System.out.println("\nAutor: %s\n", autor.getNome());
                    livrosDoAutor.forEach(this::exibeDadosLivroAutor);
                } else {
                    System.out.printf("\nO autor %s não possui livros cadastrados.\n", autor.getNome());
                }
            }
        }
    }
}

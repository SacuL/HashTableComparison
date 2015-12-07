package Estruturas.Hashing;

import Estruturas.PalavraFactory;
import Estruturas.Documento;
import Estruturas.InterfacePalavra;
import FuncoesHash.InterfaceHashing;
import Util.Strings;
import FuncoesHash.FuncaoHashingFactory;
import java.util.ArrayList;

/**
 * Índice invertido de busca
 *
 * @author Lucas
 */
public class TabelaHash {

    private final int SEED;
    private final int TAMANHO_TABELA;
    private final int TAMANHO_TABELA_MENOS_1;
    private final InterfaceHashing funcaoHashing;
    private final PalavraFactory.TipoPalavra tipoPalavra;
    private int PALAVRAS;
    private final ArrayList<Documento> documentos;
    private double logDoTotalDeDocumentos;

    final private ArrayList<InterfacePalavra>[] array;

    /**
     * Construtor
     */
    public TabelaHash(int tam, int limite, int seed, FuncaoHashingFactory.Funcao funcaoHashing, PalavraFactory.TipoPalavra tipoPalavra) {
        this.tipoPalavra = tipoPalavra;
        this.funcaoHashing = FuncaoHashingFactory.criaHashing(funcaoHashing);
        this.TAMANHO_TABELA = tam;
        this.TAMANHO_TABELA_MENOS_1 = tam - 1;
        this.SEED = seed;
        this.PALAVRAS = 0;
        this.array = (ArrayList<InterfacePalavra>[]) new ArrayList[tam];
        if (limite > 0) {
            this.documentos = new ArrayList<>(limite);
        } else {
            this.documentos = new ArrayList<>();
        }

    }

    /**
     * Insere uma nova palavra de um documento em uma dada posicao. Retorna true
     * se houve uma colisão.
     */
    public boolean insere(String palavra, int id_documento, int posicao) {

        // Caso a posicao esteja vazia cria um novo ArrayList e insere a palavra
        if (array[posicao] == null) {
            array[posicao] = new ArrayList<>();
            array[posicao].add(PalavraFactory.criaPalavra(palavra, id_documento, tipoPalavra));
            this.PALAVRAS++;
            return false;
        }

        // Procura a palavra e insere um par na palavra
        for (InterfacePalavra p : array[posicao]) {
            if (p.getTexto().equals(palavra)) {
                p.insere(id_documento);
                return false;
            }
        }

        // Se a palavra não foi encontrada cria uma nova palavra
        // Colisão!
        array[posicao].add(PalavraFactory.criaPalavra(palavra, id_documento, tipoPalavra));
        this.PALAVRAS++;
        return true;

    }

    public int getNumeroDePalavrasUnicas() {
        return this.PALAVRAS;
    }

    /**
     * Busca uma palavra no array
     */
    public InterfacePalavra buscarPalavra(String palavra) {

        // Normaliza: remove acentos, pontuação e caixa alta
        String texto = Strings.NormalizaTexto(palavra);

        // Calcula a posicao usando uma função de hashing
        long valorHashLong = funcaoHashing.hashLong(texto);

//        long valorHashLongMod = (valorHashLong % TAMANHO_TABELA);
        // Limita o valor pelo tamanho da tabela
        int valorComModint = (int) (valorHashLong & TAMANHO_TABELA_MENOS_1);

        if (array[valorComModint] != null) {
            for (InterfacePalavra p : array[valorComModint]) {
                if (texto.equals(p.getTexto())) {
                    return p;
                }
            }
        }

        return null;
    }

    public Documento getDocumento(int id_documento) {
        return documentos.get(id_documento);
    }

    public void insereDocumento(Documento doc, int id_documento) {
        this.documentos.add(id_documento, doc);
    }

    /**
     * Retorna o numero de ocorrencias de uma palavra em um documento
     */
//    public int ocorrenciasPalavraNoDocumento(String palavra, int posicaoPalavra, int id_documento) {
//
//        if (array[posicaoPalavra] != null) {
//            for (InterfacePalavra p : array[posicaoPalavra]) {
//                if (p.getTexto().equals(palavra)) {
//                    return p.numeroOcorrenciasPalavraNoDocumento(id_documento);
//                }
//            }
//        }
//        return 0;
//
//    }

    /**
     * Retorna o número de termos distintos do documento
     */
    public int numeroTermosDistintosDocumento(int id_documento) {
        return this.documentos.get(id_documento).getNumeroDeTermosDistintos();
    }

    /**
     * Calcula o log do numero total de documentos e guarda
     */
    public void calculaLog2NumeroTotalDeDocumentos() {

        this.logDoTotalDeDocumentos = Math.log(this.documentos.size()) / Math.log(2);
    }

    /**
     * Retorna o log do numero total de documentos
     */
    public double log2NumeroTotalDeDocumentos() {
        return this.logDoTotalDeDocumentos;
    }

    /**
     * Retorna quantos documentos a tabela possui
     */
    public int numeroDocumentosTotais() {
        return this.documentos.size();
    }

    /**
     * Retorna a quantidade de palavras de uma posicao do array
     */
    public int getNumeroPalavras(int indice_palavra) {
        ArrayList l = array[indice_palavra];
        if (l == null) {
            return 0;
        } else {
            return l.size();
        }
    }

    ///////////////////////////////////////////
    //// Funções para coletar estatísticas ////
    ///////////////////////////////////////////
    /**
     * Retorna quantas posições tiveram ao menos uma colisão
     */
    public int posicoesComColisao() {
        int contador = 0;
        for (int i = 0; i < TAMANHO_TABELA; i++) {
            if (array[i] != null) {
                if (array[i].size() > 1) {
                    contador++;
                }

            }

        }
        return contador;
    }

    /**
     * Retorna quantos % do array esta vazio.
     */
    public double vazio() {
        int contador = 0;
        for (int i = 0; i < TAMANHO_TABELA; i++) {
            if (array[i] == null) {
                contador++;
            }

        }
        double c = contador;
        double t = TAMANHO_TABELA;
        double percent = (c / t) * 100;

        return percent;

    }

    /**
     * Retorna o tamanho medio dos arrays de palavras
     */
    public double tamanhoMedioArrayPalavras() {
        double posicoes = 0;
        double somatorio = 0;
        for (int i = 0; i < TAMANHO_TABELA; i++) {
            if (array[i] != null) {
                somatorio = somatorio + array[i].size();
                posicoes++;
            }

        }

        double media = somatorio / posicoes;
        return media;

    }

    /**
     * Retorna o tamanho medio da estrutura de tuplas(pares)
     */
    public double tamanhoMedioArrayPares() {
        double posicoes = 0;
        double somatorio = 0;
        for (int i = 0; i < TAMANHO_TABELA; i++) {
            if (array[i] != null) {
                for (InterfacePalavra p : array[i]) {
                    somatorio = somatorio + p.numeroDocumentos();
                    posicoes++;
                }

            }

        }

        double media = (somatorio / posicoes);
        return media;
    }

}

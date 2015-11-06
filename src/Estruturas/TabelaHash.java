package Estruturas;

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
        this.SEED = seed;
        this.PALAVRAS = 0;
        this.array = (ArrayList<InterfacePalavra>[]) new ArrayList[tam];
        if (limite > 0) {
            this.documentos = new ArrayList<>(limite);
            this.logDoTotalDeDocumentos = Math.log10(limite);
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
        array[posicao].add(new PalavraList(palavra, id_documento));
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

        // Aplica normalização
        byte[] bb = Strings.BytePalavraNormalizada(texto);

        // Calcula a posicao usando uma função de hashing
        int valorHash = funcaoHashing.hash(bb, 0, bb.length, 13);

        // Limita o valor pelo tamanho da tabela
        int valorComMod = (valorHash % TAMANHO_TABELA);

        if (array[valorComMod] != null) {
            for (InterfacePalavra p : array[valorComMod]) {
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
    public int ocorrenciasPalavraNoDocumento(String palavra, int posicaoPalavra, int id_documento) {

        if (array[posicaoPalavra] != null) {
            for (InterfacePalavra p : array[posicaoPalavra]) {
                if (p.getTexto().equals(palavra)) {
                    return p.numeroOcorrenciasPalavraNoDocumento(id_documento);
                }
            }
        }
        return 0;

    }

    /**
     * Calcula o log do numero total de documentos e guarda
     */
    public void calculaLog10NumeroTotalDeDocumentos() {
        this.logDoTotalDeDocumentos = Math.log10(this.documentos.size());
    }

    /**
     * Retorna o log do numero total de documentos
     */
    public double log10NumeroTotalDeDocumentos() {
        return this.logDoTotalDeDocumentos;
    }

    ///////////////////////////////////////////
    //// Funções para coletar estatísticas ////
    ///////////////////////////////////////////
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
        double contador = 0;
        double media = 0;
        for (int i = 0; i < TAMANHO_TABELA; i++) {
            if (array[i] != null) {
                media = media + array[i].size();
                contador++;
            }

        }

        double total = media / contador;
        return total;

    }

    /**
     * Retorna o tamanho medio da estrutura de tuplas(pares)
     */
    public double tamanhoArrayPares() {
        double contador = 0;
        double media = 0;
        for (int i = 0; i < TAMANHO_TABELA; i++) {
            if (array[i] != null) {
                for (InterfacePalavra p : array[i]) {
                    media = media + p.numeroDocumentos();
                    contador++;
                }

            }

        }

        double total = (media / contador);
        return total;
    }

}

package Estruturas;

import FuncoesHash.InterfaceHashing;
import FuncoesHash.MurmurHash;
import Util.Strings;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Índice invertido de busca
 *
 * @author Lucas
 */
public class TabelaHash {

    private final int SEED;
    private final int TAMANHO;
    private final InterfaceHashing funcaoHashing;
    private int PALAVRAS;

    final private ArrayList<Palavra>[] array;

    /**
     * Construtor
     */
    public TabelaHash(int tam, int seed, InterfaceHashing funcaoHashing) {
        this.funcaoHashing = funcaoHashing;
        this.TAMANHO = tam;
        this.SEED = seed;
        this.PALAVRAS = 0;
        array = (ArrayList<Palavra>[]) new ArrayList[tam];
    }

    /**
     * Insere uma nova palavra de um documento em uma dada posicao. Retorna true
     * se houve uma colisão.
     */
    public boolean insere(String palavra, int id_documento, int posicao) {

        // Caso a posicao esteja vazia cria um novo ArrayList e insere a palavra
        if (array[posicao] == null) {
            array[posicao] = new ArrayList<>();
            array[posicao].add(new Palavra(palavra, id_documento));
            this.PALAVRAS++;
            return false;
        }

        // Procura a palavra e insere um par na palavra
        for (Palavra p : array[posicao]) {
            if (p.getTexto().equals(palavra)) {
                p.insere(id_documento);
                return false;
            }
        }

        // Se a palavra não foi encontrada cria uma nova palavra
        // Colisão!
        array[posicao].add(new Palavra(palavra, id_documento));
        this.PALAVRAS++;
        return true;

    }

    public int getNumeroDePalavrasUnicas() {
        return this.PALAVRAS;
    }

    ///////////////////////////////////////////
    //// Funções para coletar estatísticas ////
    ///////////////////////////////////////////
    /**
     * Imprime no console quantos % do array esta vazio.
     */
    public void vazio() {
        int contador = 0;
        for (int i = 0; i < TAMANHO; i++) {
            if (array[i] == null) {
                contador++;
            }

        }
        double c = contador;
        double t = TAMANHO;
        double percent = (c / t) * 100;

        System.out.println(">> " + percent + "% do array esta vazio.");

    }

    /**
     * Imprime no console o tamanho medio dos arrays de palavras
     */
    public void tamanhoArrayPalavras() {
        double contador = 0;
        double media = 0;
        for (int i = 0; i < TAMANHO; i++) {
            if (array[i] != null) {
                media = media + array[i].size();
                contador++;
            }

        }

        double total = media / contador;

        System.out.println(">> Os arrays de palavras tem em media " + total + " palavra(s).");
    }

    /**
     * Imprime no console o tamanho medio dos arrays de pares
     */
    public void tamanhoArrayPares() {
        double contador = 0;
        double media = 0;
        for (int i = 0; i < TAMANHO; i++) {
            if (array[i] != null) {
                for (Palavra p : array[i]) {
                    media = media + p.getDocumentos().size();
                    contador++;
                }

            }

        }

        double total = (media / contador);

        System.out.println(">> Os arrays de pares tem em media " + total + " par(es).");
    }

    /**
     * Busca uma palavra no array
     */
    public Palavra buscarPalavra(String palavra) {

        byte[] bb = Strings.BytePalavraNormalizada(palavra);

        int valorHash = funcaoHashing.hash(bb, 0, bb.length, SEED);

        int nn = (valorHash % TAMANHO);

        if (array[nn] != null) {
            for (Palavra p : array[nn]) {
                if (palavra.equals(p.getTexto())) {
                    return p;
                }
            }
        }

        return null;
    }

}

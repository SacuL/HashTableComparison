package Estruturas;

/**
 *
 * @author Lucas
 */
public class TabelaHash {

    int colisoes;
    int[] array;

    public TabelaHash(int tamanho) {
        colisoes = 0;
        array = new int[tamanho];
    }

    /**
     * Funcao de teste. Pseudo-insere em uma posicao.
     *
     * @param posicao
     */
    public void insere(int posicao) {

//        System.out.println("Inserindo na posicao " + posicao + " de " + array.length + " posicoes");
        if (array[posicao] == 0) {
            array[posicao] = 1;

        } else {
            colisoes++;
        }

    }

    public int colisoes() {
        return colisoes;
    }

    public int vazios() {
        int cont = 0;
        for (int i : array) {
            if (array[i] == 0) {
                cont++;
            }

        }
        return cont;
    }

}

package Estruturas;

/**
 * Estrutura que representa uma tupla id_documento - contador. id_documento é o
 * identificador do documento (baseado na posição da linha do arquivo). Contador
 * é o número de vezes que uma palavra (que contem um objeto Par) aparece no
 * documento.
 *
 * @author Lucas
 */
@Deprecated
public class Par {

    public int contador;
    public int id_documento;

    /**
     * Construtor
     *
     */
    public Par(int id_documento) {
        this.contador = 1;
        this.id_documento = id_documento;
    }

    /**
     * Incrementa em 1 o contador de ocorrências de uma palavra em um documento
     */
    public void incrementaContador() {
        this.contador++;
    }

}

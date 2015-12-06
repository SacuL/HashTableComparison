package Estruturas.Trie;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Lucas
 */
public class PalavraMapTrie {

    private final HashMap<Integer, Integer> documentos;

    /**
     * Construtor: cria um novo HashMap insere uma nova key.
     */
    public PalavraMapTrie(int id_documento) {
        this.documentos = new HashMap<>();
        this.documentos.put(id_documento, 1);
    }

    /**
     * Caso a lista de pares já contenha aquele documento, incrementa o
     * contador. Caso contrário insere um novo nó.
     */
    public void insere(int id_documento) {

        Integer v = this.documentos.get(id_documento);

        if (v == null) {
            this.documentos.put(id_documento, 1);
        } else {
            this.documentos.put(id_documento, v + 1);
        }
    }

    public HashMap<Integer, Integer> getDocumentos() {
        return documentos;
    }

    /**
     * Imprime todos as ocorrencias da palavra.
     */
    public void imprimePares() {
        System.out.println("A palavra oif encontrada nos seguintes documentos:");
        for (Map.Entry<Integer, Integer> i : this.documentos.entrySet()) {
            System.out.print(i.getKey() + " - ");
        }
        System.out.println();
    }

    /**
     * Retorna o numero de documentos em que a palavra ocorre
     */
    public int numeroDocumentos() {
        if (this.documentos != null) {
            return this.documentos.size();
        }
        return 0;
    }

    /**
     * Retorna o numero de ocorrencias da palavra no documento
     */
    public int numeroOcorrenciasPalavraNoDocumento(int id_documento) {
        return this.documentos.get(id_documento);
    }

    /**
     * Retorna um iterator para percorrer a estrutura
     */
    public Iterator getIterator() {
        if (this.documentos == null || this.documentos.isEmpty()) {
            return null;
        }

        return this.documentos.entrySet().iterator();
    }

}

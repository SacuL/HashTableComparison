package Estruturas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Estrutura que representa uma palavra e suas ocorrências em vários documentos.
 * Possui um String texto que representa a palavra em sí e um HashMap onde a
 * chave é o id do documento e o valor um contador de ocorrências da palavra em
 * um documento.
 *
 * @author Lucas
 */
public class PalavraMap implements InterfacePalavra {

    private String texto;
    private final HashMap<Integer, Integer> documentos;

    /**
     * Construtor: cria um novo HashMap insere uma nova key.
     */
    public PalavraMap(String texto, int id_documento) {
        this.texto = texto;
        this.documentos = new HashMap<>();
        this.documentos.put(id_documento, 1);
    }

    /**
     * Caso a lista de pares já contenha aquele documento, incrementa o
     * contador. Caso contrário insere um novo nó.
     */
    public void insere(int id_documento) {

        if (this.documentos.containsKey(id_documento)) {
            this.documentos.put(id_documento, documentos.get(id_documento) + 1);
        } else {
            this.documentos.put(id_documento, 1);
        }
    }

    /*
     * Retorna a palavra em sí;
     */
    public String getTexto() {
        return texto;
    }

    public HashMap<Integer, Integer> getDocumentos() {
        return documentos;
    }

    /**
     * Compara se dois objetos Palavra são iguais. A condição é a string palavra
     * que ambos contém ser a mesma, independente do HashMap.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PalavraMap other = (PalavraMap) obj;
        return Objects.equals(this.texto, other.texto);
    }

    /**
     * Imprime todos os documentos do HashMap.
     */
    public void imprimePares() {
        System.out.println("A palavra " + this.texto + " foi encontrada nos seguintes documentos:");
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

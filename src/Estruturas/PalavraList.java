package Estruturas;

import Estruturas.Par;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * Estrutura que representa uma palavra e suas ocorrências em vários documentos.
 * Possui um String texto que representa a palavra em sí e uma ArrayList de Par
 * onde cada par representa um número de ocorrências da palavra em um documento.
 *
 * @author Lucas
 */
public class PalavraList implements InterfacePalavra {

    private String texto;
    private final ArrayList<Par> documentos;

    /**
     * Construtor: cria um novo ArrayList de pares e insere um novo par.
     */
    public PalavraList(String texto, int id_documento) {
        this.texto = texto;
        documentos = new ArrayList<>();
        documentos.add(new Par(id_documento));
    }

    /**
     * Caso a lista de pares já contenha aquele documento, incrementa o
     * contador. Caso contrário insere um novo nó.
     */
    public void insere(int id_documento) {

        // Busca o par e incrementa o contador
        for (Par p : documentos) {
            if (p.id_documento == id_documento) {
                p.incrementaContador();
                return;
            }
        }

        // Se o par nao existir, insere um novo par
        documentos.add(new Par(id_documento));
    }

    public String getTexto() {
        return texto;
    }

    public ArrayList<Par> getDocumentos() {
        return documentos;
    }

    /**
     * Compara se dois objetos Palavra são iguais. A condição é a string palavra
     * que ambos contém ser a mesma, independente do array de pares.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PalavraList other = (PalavraList) obj;
        return Objects.equals(this.texto, other.texto);
    }

    /**
     * Imprime todos os pares do array..
     */
    public void imprimePares() {
        System.out.println("A palavra " + this.texto + " foi encontrada nos seguintes documentos:");
        for (Par p : this.documentos) {
            System.out.print(p.id_documento + " - ");
        }
        System.out.println();
    }

    public int numeroDocumentos() {
        if (this.documentos != null) {
            return this.documentos.size();
        }
        return 0;
    }

    /**
     *
     */
    public int numeroOcorrenciasPalavraNoDocumento(int id_documento) {
        return 0;
    }

    /**
     * Retorna um iterator para percorrer a estrutura
     */
    public Iterator getIterator() {
        if (this.documentos == null || this.documentos.isEmpty()) {
            return null;
        }
        return this.documentos.iterator();
    }
}

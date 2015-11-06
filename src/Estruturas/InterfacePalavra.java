package Estruturas;

import java.util.Iterator;

/**
 *
 * @author Lucas
 */
public interface InterfacePalavra {

    /**
     * Insere uma palavra (ou incrementa o contador)
     */
    public void insere(int id_documento);

    /*
     * Retorna a palavra em sí;
     */
    public String getTexto();

    // Metodo para teste
    public void imprimePares();

    /**
     * Retorna o numero de documentos em que a palavra ocorre
     */
    public int numeroDocumentos();

    /**
     * Retorna o numero de ocorrencias da palavra no documento
     */
    public int numeroOcorrenciasPalavraNoDocumento(int id_documento);

    /**
     * Retorna um iterator para percorrer a estrutura
     */
    public Iterator getIterator();
}

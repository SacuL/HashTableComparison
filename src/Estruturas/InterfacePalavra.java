package Estruturas;

/**
 *
 * @author Lucas
 */
public interface InterfacePalavra {

    public void insere(int id_documento);

    public String getTexto();

    public void imprimePares();

    public int numeroDocumentos();

    public int numeroOcorrenciasPalavraNoDocumento(int id_documento);
}

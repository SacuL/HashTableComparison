package GUI;

import Estruturas.InterfacePalavra;
import Estruturas.TabelaHash;
import FuncoesHash.InterfaceHashing;
import javax.swing.SwingWorker;
import java.util.List;
import javax.swing.JTextArea;

/**
 *
 * @author Lucas
 */
public class BuscarPalavra extends SwingWorker<Integer, String> {

    private final JTextArea log;
    private final String[] palavras;
    private final TabelaHash tbHash;

    /**
     * Construtor
     */
    public BuscarPalavra(JTextArea log, String[] palavras, TabelaHash tbHash) {
        this.log = log;
        this.palavras = palavras;
        this.tbHash = tbHash;
    }

    /**
     * Método que realiza a busca das palavra.
     */
    @Override
    protected Integer doInBackground() throws Exception {
        
        

        for (String s : palavras) {

            // Busca o objeto Palavra da palavra correspondente
            InterfacePalavra p = tbHash.buscarPalavra(s);
            publish("Encontrei a palavra " + p.getTexto());
            publish("A palavra " + p.getTexto() + " aparece em " + p.numeroDocumentos() + " documentos");
        }
        return null;
    }

    /**
     * Método que escreve as chamadas do método publish.
     */
    @Override
    protected void process(final List<String> chunks) {
        for (final String string : chunks) {
            log.append(string + "\n");
        }
    }

}

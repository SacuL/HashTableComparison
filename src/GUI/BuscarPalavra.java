package GUI;

import Estruturas.InterfacePalavra;
import Estruturas.TabelaHash;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.SwingWorker;
import java.util.List;
import java.util.Map;
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

        long inicio = System.nanoTime();

        double logN = tbHash.log10NumeroTotalDeDocumentos();

        // Cria um hashmap para guardar: id_documento : relevância
        HashMap<Integer, Double> docs = new HashMap<>();

        for (String s : palavras) {

            // Busca o objeto Palavra da palavra correspondente
            InterfacePalavra p = tbHash.buscarPalavra(s);
            if (p == null) {
                publish("A palavra " + s + " não foi encontrada em nenhum documento!");
                continue;
            }

            int numeroDocumentosPalavra = p.numeroDocumentos();
            publish("A palavra " + s + " foi encontrada em " + numeroDocumentosPalavra + " documentos.");
//            publish("Encontrei a palavra " + p.getTexto());
//            publish("A palavra " + p.getTexto() + " aparece em " + numeroDocumentosPalavra + " documentos");

            Iterator it = p.getIterator();
            Object o;
            while (it.hasNext()) {
                o = it.next();

                // Calcular o peso do termo em cada documento
                Map.Entry<Integer, Integer> tupla = (Map.Entry<Integer, Integer>) o;

                int id_documento = tupla.getKey();
                int ocorrencias = tupla.getValue();

                double pesoDoTermoNoDocumento = ocorrencias * logN / numeroDocumentosPalavra;

//                publish("A palavra " + s + " ocorre " + ocorrencias + " vez(es) no documento " + id_documento);
//                publish("O peso da palavra " + s + " no documento " + id_documento + " eh: " + pesoDoTermoNoDocumento);
                if (docs.containsKey(id_documento)) {
//                    publish(">> O peso anterior do documento " + id_documento + " era " + docs.get(id_documento));
                    docs.put(id_documento, docs.get(id_documento) + pesoDoTermoNoDocumento);
                } else {
                    docs.put(id_documento, pesoDoTermoNoDocumento);
                }
//                publish("O peso do documento " + id_documento + " agora eh: " + docs.get(id_documento));

            }

        }

        // Como o somatório já foi calculado, basta dividir cada relevância 
        // pelo número de termos distintos de cada documento
        for (Map.Entry<Integer, Double> i : docs.entrySet()) {
            // Busca o número de termos distintos do documento
            int numTermosDistintosDoDocumento = tbHash.numeroTermosDistintosDocumento(i.getKey());

            // Substitui pelo novo valor
            docs.put(i.getKey(), (i.getValue() / numTermosDistintosDoDocumento));
        }

        publish("Resultado (nome - relevância): ");
        List mapKeys = new ArrayList(docs.keySet());
        List mapValues = new ArrayList(docs.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        // Ineficiente
        for (int i = mapValues.size() - 1; i >= 0; i--) {

            Object val = mapValues.get(i);
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = docs.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)) {
                    docs.remove(key);
                    mapKeys.remove(key);

                    publish(tbHash.getDocumento(Integer.parseInt(key.toString())).getNome() + "\t\t" + comp1);
                    break;
                }

            }

        }

        long fim = System.nanoTime();
        double total = (fim - inicio);
        double seconds = ((double) total / 1000000000);
        publish(mapValues.size() + " resultados em " + (new DecimalFormat("#.##########").format(seconds)) + " segundo(s).");
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

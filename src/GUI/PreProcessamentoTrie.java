package GUI;

import Estruturas.Documento;
import Estruturas.Hashing.TabelaHash;
import Estruturas.PalavrasBanidas;
import Estruturas.Trie.ASCII_Trie;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import Util.Strings;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Lucas
 */
public class PreProcessamentoTrie extends SwingWorker<ASCII_Trie, String> {

    private final String caminhoArquivo;
    private final int numero_documentos;
    private final JTextArea log;
    private final Principal pai;
    private long startTime;
    private long endTime;
    private final PalavrasBanidas palavras_banidas;

    private ASCII_Trie trie;

    /**
     * Construtor
     */
    public PreProcessamentoTrie(String caminhoArquivo, int numero_documentos, JTextArea log, Principal pai, String lingua) {
        this.caminhoArquivo = caminhoArquivo;
        this.log = log;
        this.pai = pai;
        this.endTime = -1;
        this.numero_documentos = numero_documentos;
        // Inicializa a trie com 58 possíveis caracteres da tabela ASCII
        this.trie = new ASCII_Trie(58, numero_documentos);
        inicializaArrayAux();
        this.palavras_banidas = new PalavrasBanidas(lingua);
    }

    /**
     * Método que realiza o Pre Processamento.
     */
    @Override
    protected ASCII_Trie doInBackground() throws Exception {
        startTime = System.currentTimeMillis();

        publish("\n\n--== INICIANDO ==--\nAbrindo arquivo " + caminhoArquivo);

        FileInputStream stream;
        try {
            stream = new FileInputStream(caminhoArquivo);

        } catch (FileNotFoundException ex) {
            publish("Arquivo nao encontrado!");
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        int primeiroEspaco;
        int segundoEspaco;
        int numero_de_palavras = 0;
        int numero_de_palavras_puladas = 0;
        int contaLinhas = 0;
        int porcentagem = -1;

        double umPorcento = numero_documentos / 100;
        double porcentual = umPorcento;

        try {
            String linha;
            while ((linha = in.readLine()) != null && (contaLinhas <= numero_documentos || numero_documentos == -1)) {

                // Anda com a barra de progresso
                if (numero_documentos != -1 && (contaLinhas >= porcentual)) {
                    porcentagem++;
                    setProgress(porcentagem);
                    porcentual = porcentual + umPorcento;
                }

                // Ignora linhas sem conteudo
                if (linha.length() < 77) {
                    publish("Linha nao usada: " + linha);
                } else {

                    primeiroEspaco = linha.indexOf(' ');
                    segundoEspaco = (linha.substring(primeiroEspaco + 1)).indexOf(' ');

                    // Seleciona o nome do documento
                    String nome = linha.substring(29, primeiroEspaco - 1);

                    // Cria um objeto Documento
                    Documento doc = new Documento(nome);
                    int id_documento = contaLinhas - 1;

                    // Seleciona a parte do texto entre as aspas
                    String texto = linha.substring(primeiroEspaco + segundoEspaco + 3, linha.length() - 6);

                    // Normaliza: remove acentos, pontuação e caixa alta
                    texto = Strings.NormalizaTexto(texto);

                    // Encontra as palavras
                    String[] palavras = texto.split(" ");

                    numero_de_palavras = numero_de_palavras + palavras.length;

                    // Cria um HashSet para identificar as palavras unicas do documento
                    HashSet<String> palavrasUnicas = new HashSet<>();

                    for (String string : palavras) {
                        String s = string;

                        // Pula palavra:
                        // - Se for vazia ou contes apenas com espaços
                        // - Que não pode ser representada usando apenas caracteres ASCII
                        // - Se for uma das 90 palavras mais comuns
                        if (s.isEmpty() || (s.replaceAll(" ", "")).isEmpty() || !isPureAscii(s) || this.palavras_banidas.contains(s)) {
                            numero_de_palavras_puladas++;
                            continue;
                        }

                        // Ajusta para o tamanho de 20 caracteres (completando espaços caso necessário)
                        if (s.length() < 20) {
                            s = s + arrayAux[s.length()];
                        } else {
                            s = s.substring(0, 20);
                        }

                        // Insere no hashset para contar palavras únicas
                        palavrasUnicas.add(s);

                        // Insere na trie
                        trie.insere(s, id_documento);

//                        Scanner keyboard = new Scanner(System.in);
//                        keyboard.nextInt();
                    }

                    // Conta palavras únicas
                    doc.setNumeroDeTermosDistintos(palavrasUnicas.size());
                    // Insere o objeto Documento
                    trie.insereDocumento(doc, id_documento);
                }
                contaLinhas++;

            }
        } catch (IOException ex) {
            publish("Um erro ocorreu: " + ex.getMessage());
            return null;
        }

        trie.imprimeChaves();
        trie.calculaLog2NumeroDocumentos();

        publish("Numero de linhas arquivo: " + contaLinhas);
        publish("Numero total de palavras: " + numero_de_palavras);
        double total = (double) numero_de_palavras;
        double puladas = (double) numero_de_palavras_puladas;
        double inseridas = total - puladas;
        double pPalavrasPuladas = 100 * puladas / total;
        double pPalavrasInseridas = 100 * inseridas / total;
        publish("Numero de palavras   puladas: " + numero_de_palavras_puladas + " (" + pPalavrasPuladas + "%)");
        publish("Numero de palavras inseridas: " + (numero_de_palavras - numero_de_palavras_puladas) + " (" + pPalavrasInseridas + "%)");

        endTime = System.currentTimeMillis();
        return trie;
    }

    static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1

    /**
     * Método que verifica se uma string pode ser representada usando apenas
     * caracteres ASCII
     */
    public static boolean isPureAscii(String v) {
        return asciiEncoder.canEncode(v);
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

    /**
     * Método chamado ao final do processamento do método doInBackground.
     * Calcula o tempo de execução e envia a trie para o JFrame Principal.
     */
    @Override
    protected void done() {
        if (endTime < 0) {
            return;
        }
        long segundosFinais = (endTime - startTime) / 1000;
        int minutosFinais = 0;
        while (segundosFinais >= 60) {
            segundosFinais = segundosFinais - 60;
            minutosFinais++;
        }
        if (minutosFinais > 0) {
            publish("Tempo de execução: " + minutosFinais + " minuto(s) e " + segundosFinais + " segundo(s).");
        } else {
            publish("Tempo de execução: " + segundosFinais + " segundo(s).");
        }

        try {
            pai.setTrie(get());
        } catch (InterruptedException | ExecutionException ex) {
            publish("Um erro ocorreu:\n" + ex.getMessage());
        } finally {
            pai.setEnabled(true);
        }
    }

    // Array auxiliar para otimizar o processo concatenação de 
    // espaços em palavras com tamanho menor que 20
    private final String[] arrayAux = new String[20];

    /**
     * Inicializa o array auxiliar de concatenação de espaços
     */
    private void inicializaArrayAux() {

        arrayAux[1] = "                   ";
        arrayAux[2] = "                  ";
        arrayAux[3] = "                 ";
        arrayAux[4] = "                ";
        arrayAux[5] = "               ";
        arrayAux[6] = "              ";
        arrayAux[7] = "             ";
        arrayAux[8] = "            ";
        arrayAux[9] = "           ";
        arrayAux[10] = "          ";
        arrayAux[11] = "         ";
        arrayAux[12] = "        ";
        arrayAux[13] = "       ";
        arrayAux[14] = "      ";
        arrayAux[15] = "     ";
        arrayAux[16] = "    ";
        arrayAux[17] = "   ";
        arrayAux[18] = "  ";
        arrayAux[19] = " ";
    }

}

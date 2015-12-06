package GUI;

import Estruturas.Documento;
import Estruturas.PalavraFactory;
import Estruturas.Hashing.TabelaHash;
import Estruturas.PalavrasBanidas;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import FuncoesHash.FuncaoHashingFactory;
import FuncoesHash.InterfaceHashing;
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
public class PreProcessamento extends SwingWorker<TabelaHash, String> {

    private final String caminhoArquivo;
    private final int tamanhoTabela;
    private final int tamanhoTabelaMenos1;
    private final int limite;
    private final PalavraFactory.TipoPalavra tipoPalavra;
    private final FuncaoHashingFactory.Funcao tipoFuncaoHashing;
    private final InterfaceHashing funcaoHashing;
    private final JTextArea log;
    private final Principal pai;
    private long startTime;
    private long endTime;
    private final PalavrasBanidas palavras_banidas;

    /**
     * Construtor
     */
    public PreProcessamento(String caminhoArquivo, int tamanhoTabela, int limite, PalavraFactory.TipoPalavra tipoPalavra, FuncaoHashingFactory.Funcao tipoFuncaoHashing, JTextArea log, Principal pai, String lingua) {
        this.caminhoArquivo = caminhoArquivo;

        // Encontra uma potência de 2 >= tamanhoTabela
        int capacity = 1;
        while (capacity < tamanhoTabela) {
            capacity <<= 1;
        }

        this.tamanhoTabela = capacity;
        this.tamanhoTabelaMenos1 = capacity - 1;

        if (limite < 0) {
            this.limite = -1;
        } else {
            this.limite = limite;
        }

        this.tipoPalavra = tipoPalavra;
        this.tipoFuncaoHashing = tipoFuncaoHashing;
        this.funcaoHashing = FuncaoHashingFactory.criaHashing(tipoFuncaoHashing);
        this.log = log;
        this.pai = pai;
        this.endTime = -1;
        inicializaArrayAux();

        this.palavras_banidas = new PalavrasBanidas(lingua);
    }

    /**
     * Método que realiza o Pre Processamento.
     */
    @Override
    protected TabelaHash doInBackground() throws Exception {
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

        TabelaHash tbHash = new TabelaHash(tamanhoTabela, limite, 13, tipoFuncaoHashing, tipoPalavra);

        int primeiroEspaco;
        int segundoEspaco;
        int numero_de_palavras = 0;
        int numero_de_palavras_puladas = 0;
        int contaLinhas = 0;
        int porcentagem = -1;
        int colisoes = 0;

        try {
            String linha;
            while ((linha = in.readLine()) != null && (contaLinhas <= limite || limite == -1)) {

                // Anda com a barra de progresso
                if (limite != -1 && (contaLinhas % (((double) ((double) limite) / 100)) == 0)) {
                    porcentagem++;
                    setProgress(porcentagem);
                }

                if (linha.length() < 77) {
                    publish("Linha nao usada: " + linha);
                } else {

                    primeiroEspaco = linha.indexOf(' ');
                    segundoEspaco = (linha.substring(primeiroEspaco + 1)).indexOf(' ');

                    // Seleciona o nome do documento
                    String nome = linha.substring(29, primeiroEspaco - 1);

                    // Cria um objeto Documento
                    Documento doc = new Documento(nome);

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

                        if (s.isEmpty() || (s.replaceAll(" ", "")).isEmpty() || !isPureAscii(s) || this.palavras_banidas.contains(s)) {
                            numero_de_palavras_puladas++;
                            continue;
                        }

                        // Insere no hashset para contar palavras únicas
                        palavrasUnicas.add(s);

                        // Calcula a posicao usando uma função de hashing
//                        int valorHash = funcaoHashing.hash(s);
                        long valorHash = funcaoHashing.hashLong(s);
                        // Limita o valor pelo tamanho da tabela
//                        int valorComMod = (valorHash % tamanhoTabela);
//                        long valorComMod = (valorHash % tamanhoTabela);
//                        // Casting seguro pois tamanho tabela < limite int32
//                        int valorIntComMod = (int) valorComMod;
                        // Otimização: substitui o mod
                        int valorIntComMod = (int) (valorHash & tamanhoTabelaMenos1);
//                         Insere no hashing
                        if (tbHash.insere(s, contaLinhas - 1, valorIntComMod)) {
                            colisoes++;
                        }
                    }

                    // Conta palavras únicas
                    doc.setNumeroDeTermosDistintos(palavrasUnicas.size());

                    // Insere o objeto Documento
                    tbHash.insereDocumento(doc, contaLinhas - 1);

                }
                contaLinhas++;

            }
        } catch (IOException ex) {
            publish("Um erro ocorreu: " + ex.getMessage());
            return null;
        }

        if (tbHash.getNumeroDePalavrasUnicas() == 0) {
            publish("Nada foi inserido!");
            return null;
        }

        tbHash.calculaLog2NumeroTotalDeDocumentos();

        publish("Numero de linhas arquivo: " + contaLinhas);
        publish("Numero de colisoes: " + colisoes);
        publish("Numero de palavras unicas: " + tbHash.getNumeroDePalavrasUnicas());
        publish("Porcentual de colisões: " + (100 * colisoes / tbHash.getNumeroDePalavrasUnicas()));
        publish("Numero total de palavras: " + numero_de_palavras);
        publish("Numero de palavras puladas: " + numero_de_palavras_puladas);
        publish("Numero de palavras inserid: " + (numero_de_palavras - numero_de_palavras_puladas));
        publish("Numero de espaços totais: " + (tamanhoTabela));

        double qtd = ((double) colisoes / (double) tamanhoTabela);
        publish(" >> Quantidade Média de Colisões Totais: " + qtd);

        double posComCol = tbHash.posicoesComColisao();
        if (posComCol != 0) {
            double distMed = ((double) colisoes / tbHash.posicoesComColisao());
            publish(" >> Distribuição Média das Colisões: " + distMed);
        } else {
            publish(" >> Distribuição Média das Colisões: não houve colisão!");
        }

        publish(tbHash.vazio() + "% do array está vazio.");
        publish("Tamanho medio das tuplas: " + tbHash.tamanhoMedioArrayPares());
        publish("Tamanho medio da estrutura de palavras " + tbHash.tamanhoMedioArrayPalavras());

        endTime = System.currentTimeMillis();
        return tbHash;
    }

    private String[] arrayAux = new String[20];

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

    static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder(); // or "ISO-8859-1" for ISO Latin 1

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
     * Método chamado ao final do processamento do método doInBackground. Envia
     * a tabela hash para o JFrame Principal.
     */
    @Override
    protected void done() {
        if (endTime < 0) {
            return;
        }
        long segundosFinais = (endTime - startTime) / 1000;
        int minutosFinais = 0;
        while (segundosFinais > 60) {
            segundosFinais = segundosFinais - 60;
            minutosFinais++;
        }
        if (minutosFinais > 0) {
            publish("Tempo de execução: " + minutosFinais + ":" + segundosFinais);
        } else {
            publish("Tempo de execução: " + segundosFinais + " segundo(s).");
        }

        try {
            pai.setTabelaHash(get());
        } catch (InterruptedException | ExecutionException ex) {
            publish("Um erro ocorreu:\n" + ex.getMessage());
        } finally {
            pai.setEnabled(true);
        }
    }

}

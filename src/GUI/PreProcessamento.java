package GUI;

import Estruturas.Documento;
import Estruturas.PalavraFactory;
import Estruturas.TabelaHash;
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
    private final int limite;
    private final PalavraFactory.TipoPalavra tipoPalavra;
    private final FuncaoHashingFactory.Funcao tipoFuncaoHashing;
    private final InterfaceHashing funcaoHashing;
    private final JTextArea log;
    private final int SEED;
    private Principal pai;
    private long startTime;
    private long endTime;

    /**
     * Construtor
     */
    public PreProcessamento(String caminhoArquivo, int tamanhoTabela, int limite, PalavraFactory.TipoPalavra tipoPalavra, FuncaoHashingFactory.Funcao tipoFuncaoHashing, JTextArea log, Principal pai) {
        this.caminhoArquivo = caminhoArquivo;
        this.tamanhoTabela = tamanhoTabela;

        if (limite < 0) {
            this.limite = -1;
        } else {
            this.limite = limite;
        }

        this.tipoPalavra = tipoPalavra;
        this.tipoFuncaoHashing = tipoFuncaoHashing;
        this.funcaoHashing = FuncaoHashingFactory.criaHashing(tipoFuncaoHashing);
        this.log = log;
        this.SEED = 13;
        this.pai = pai;
        this.endTime = -1;
    }

    /**
     * Método que realiza o Pre Processamento.
     */
    @Override
    protected TabelaHash doInBackground() throws Exception {
        startTime = System.currentTimeMillis();

        publish("\n\n--== INICIANDO ==--\nAbrindo arquivo " + caminhoArquivo);

        FileInputStream stream = null;
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

                if (limite != -1 && (contaLinhas % (limite / 100) == 0)) {
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

                        if (s.isEmpty() || (s.replaceAll(" ", "")).isEmpty()) {
                            numero_de_palavras_puladas++;
                            continue;
                        }

                        // Insere no hashset para contar palavras únicas
                        palavrasUnicas.add(s);

                        // Aplica normalização
                        byte[] bb = Strings.BytePalavraNormalizada(s);

                        // Calcula a posicao usando uma função de hashing
                        int valorHash = funcaoHashing.hash(bb, 0, bb.length, SEED);

                        // Limita o valor pelo tamanho da tabela
                        int valorComMod = (valorHash % tamanhoTabela);

                        // Insere no hashing
                        if (tbHash.insere(s, contaLinhas - 1, valorComMod)) {
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
            // Nada foi inserido
            return null;
        }

        tbHash.calculaLog10NumeroTotalDeDocumentos();

        publish("Numero de linhas arquivo: " + contaLinhas);
        publish("Numero de colisoes: " + colisoes);
        publish("Numero de palavras unicas: " + tbHash.getNumeroDePalavrasUnicas());
        publish("Porcentual de colisões: " + (100 * colisoes / tbHash.getNumeroDePalavrasUnicas()));
        publish("Numero total de palavras: " + numero_de_palavras);
        publish("Numero de palavras puladas: " + numero_de_palavras_puladas);
        publish("Numero de palavras inserid: " + (numero_de_palavras - numero_de_palavras_puladas));
        publish("Numero de espaços totais: " + (tamanhoTabela));

        endTime = System.currentTimeMillis();
        return tbHash;
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
        }
    }

}

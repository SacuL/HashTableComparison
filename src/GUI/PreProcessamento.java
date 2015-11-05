package GUI;

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
import java.util.List;
import javax.swing.JProgressBar;

/**
 *
 * @author Lucas
 */
public class PreProcessamento extends SwingWorker<Integer, String> {

    private final String caminhoArquivo;
    private final int tamanhoTabela;
    private final int limite;
    private final PalavraFactory.TipoPalavra tipoPalavra;
    private final FuncaoHashingFactory.Funcao tipoFuncaoHashing;
    private final InterfaceHashing funcaoHashing;
    private final JTextArea log;
    private final int SEED;

    public PreProcessamento(String caminhoArquivo, int tamanhoTabela, int limite, PalavraFactory.TipoPalavra tipoPalavra, FuncaoHashingFactory.Funcao tipoFuncaoHashing, JTextArea log) {
        this.caminhoArquivo = caminhoArquivo;
        this.tamanhoTabela = tamanhoTabela;
        this.limite = limite;
        this.tipoPalavra = tipoPalavra;
        this.tipoFuncaoHashing = tipoFuncaoHashing;
        this.funcaoHashing = FuncaoHashingFactory.criaHashing(tipoFuncaoHashing);
        this.log = log;
        this.SEED = 13;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        publish("Abrindo arquivo " + caminhoArquivo);

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(caminhoArquivo);

        } catch (FileNotFoundException ex) {
            publish("Arquivo nao encontrado!");
            return null;
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        TabelaHash tbHash = new TabelaHash(tamanhoTabela, 13, tipoFuncaoHashing, tipoPalavra);

        int primeiroEspaco;
        int segundoEspaco;
        int numero_de_palavras = 0;
        int numero_de_palavras_puladas = 0;
        int contaLinhas = 0;
        int porcentagem = -1;
        int colisoes = 0;

        try {
            String linha;
            while ((linha = in.readLine()) != null && contaLinhas < limite) {

                if (contaLinhas % (limite / 100) == 0) {
                    porcentagem++;
                    setProgress(porcentagem);
                }

                if (linha.length() < 77) {
                    publish("Linha nao usada: " + linha);
                } else {

                    primeiroEspaco = linha.indexOf(' ');
                    segundoEspaco = (linha.substring(primeiroEspaco + 1)).indexOf(' ');

                    /// Seleciona o titulo
                    String titulo = linha.substring(1, primeiroEspaco - 1);

                    /// Seleciona a parte do texto entre as aspas
                    String texto = linha.substring(primeiroEspaco + segundoEspaco + 3, linha.length() - 6);

                    /// Normaliza: remove acentos, pontuação e caixa alta
                    texto = Strings.NormalizaTexto(texto);

                    /// Encontra as palavras
                    String[] palavras = texto.split(" ");

                    numero_de_palavras = numero_de_palavras + palavras.length;

                    for (String string : palavras) {
                        String s = string;

                        if (s.isEmpty() || (s.replaceAll(" ", "")).isEmpty()) {
                            numero_de_palavras_puladas++;
                            continue;
                        }

                        byte[] bb = Strings.BytePalavraNormalizada(s);

                        int valorHash = funcaoHashing.hash(bb, 0, bb.length, SEED);

                        int valorComMod = (valorHash % tamanhoTabela);

                        if (tbHash.insere(s, contaLinhas, valorComMod)) {
                            colisoes++;
                        }
                    }
                }

                contaLinhas++;

            }
        } catch (IOException ex) {
            publish("Um erro ocorreu: " + ex.getMessage());
            return null;
        }

        publish("Numero de  linhas  arquivo: " + contaLinhas);
        publish("Numero     de     colisoes: " + colisoes);
        publish("Numero de palavras  unicas: " + tbHash.getNumeroDePalavrasUnicas());
        publish("Porcentual   de   colisões: " + (100 * colisoes / tbHash.getNumeroDePalavrasUnicas()));
        publish("Numero total de   palavras: " + numero_de_palavras);
        publish("Numero de palavras puladas: " + numero_de_palavras_puladas);
        publish("Numero de palavras inserid: " + (numero_de_palavras - numero_de_palavras_puladas));
        publish("Numero de espaços totais: " + (tamanhoTabela));

        return null;
    }

    @Override
    protected void process(final List<String> chunks) {
        for (final String string : chunks) {
            log.append(string + "\n");
        }
    }

}

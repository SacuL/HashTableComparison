package trabalhoaed;

import Estruturas.Palavra;
import Estruturas.TabelaHash;
import FuncoesHash.InterfaceHashing;
import FuncoesHash.MurmurHash;
import Util.Strings;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 *
 * @author Lucas
 */
public class PreProcessamento {

    public static void main(String[] args) {

        InterfaceHashing funcaoHashing = new MurmurHash();

        FileInputStream stream = null;

        try {
            stream = new FileInputStream("C:\\Users\\Lucas\\Desktop\\short-abstracts_en.ttl");

        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo nao encontrado!");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        /////////////////////
        /// Configurações ///
        /////////////////////
        int TAMANHO_TABELA = 1000000;
        int LIMITE = 400000;
        int SEED = 13;
        // int TOTAL_LINHAS = 4305030;

        TabelaHash tbHash = new TabelaHash(TAMANHO_TABELA, SEED, funcaoHashing);

        int primeiroEspaco;
        int segundoEspaco;
        int numero_de_palavras = 0;
        int numero_de_palavras_puladas = 0;
        int contaLinhas = 0;
        int porcentagem = -1;
        int colisoes = 0;

        try {
            String linha;
            while ((linha = in.readLine()) != null && contaLinhas < LIMITE) {

                if (contaLinhas % (LIMITE / 100) == 0) {
                    porcentagem++;
                    System.out.println(porcentagem + "% ...");
                }

                if (linha.length() < 77) {
                    System.out.println("Linha nao usada: " + linha);
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

                        int valorComMod = (valorHash % TAMANHO_TABELA);

                        if (tbHash.insere(s, contaLinhas, valorComMod)) {
                            colisoes++;
                        }
                    }
                }

                contaLinhas++;

            }
        } catch (IOException ex) {
            System.out.println("IO Exception!");
        }

        //System.out.println("Tamanho do hashset: " + hashSet.size());
        System.out.println(
                "Numero de  linhas  arquivo: " + contaLinhas);
        System.out.println(
                "Numero     de     colisoes: " + colisoes);
        System.out.println(
                "Numero de palavras  unicas: " + tbHash.getNumeroDePalavrasUnicas());
        System.out.println(
                "Porcentual   de   colisões: " + (100 * colisoes / tbHash.getNumeroDePalavrasUnicas()));
        System.out.println(
                "Numero total de   palavras: " + numero_de_palavras);
        System.out.println(
                "Numero de palavras puladas: " + numero_de_palavras_puladas);
        System.out.println(
                "Numero de palavras inserid: " + (numero_de_palavras - numero_de_palavras_puladas));
//        System.out.println(
//                "Numero de espaços vazios: " + tbHash.vazios());
//        System.out.println(
//                "Numero de espaços preenc: " + tbHash.preenchidos());
        System.out.println(
                "Numero de espaços totais: " + (TAMANHO_TABELA));

        tbHash.vazio();
        tbHash.tamanhoArrayPalavras();
        tbHash.tamanhoArrayPares();

//        Palavra p = tbHash.buscarPalavra("house");
//        p.imprimePares();

    }

}

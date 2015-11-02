package trabalhoaed;

import Estruturas.TabelaHash;
import org.apache.commons.lang3.StringUtils;
import FuncoesHash.MurmurHash;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Lucas
 */
public class PreProcessamento {

    public static void main(String[] args) {

        String teste = "qwertyuiopasdfghjklqwdqwdqwdqw";

        if (teste.length() > 20) {
            teste = teste.substring(0, 20);
        }
        while (teste.length() < 20) {
            teste = teste + ' ';
        }

        byte[] b = teste.getBytes();

        System.out.println("b= " + b);
        System.out.println("tamanho= " + b.length);

        for (byte y : b) {
            System.out.println("Byte: " + y);
        }

        int valor = MurmurHash.murmurhash3x8632(b, 0, 4, 1);

        int n = valor % 100;

        System.out.println("Hash: " + valor);
        System.out.println("n: " + n);

        if (true) {
            return;
        }

//        String teste = "Anthropology /ænθrɵˈpɒlədʒi/ is the scientific study of humans, past and present, that draws and builds upon knowledge from the social sciences, life sciences, and humanities.Since the work of Franz Boas and Bronisław Malinowski in the late 19th and early 20th centuries, social anthropology in Great Britain and cultural anthropology in the US have been distinguished from ethnology and from other social sciences by its emphasis on cross-cultural comparisons, long-term in-depth examination of context, and the importance it places on participant-observation or experiential immersion in the area of research. ";
//        String teste2 = teste.replaceAll("[.,/\\\\()\\[\\]]", " ");
//        String[] teste3 = teste2.split(" ");
//        System.out.println(teste);
//        System.out.println(teste2);
//        System.out.println(teste2.toLowerCase());
//        System.out.println("Palavras: " + teste3.length);
//        int i = 0;
//        for (String s : teste3) {
//            System.out.println("[" + i + "] " + s);
//            i++;
//        }
//        Pattern pat = Pattern.compile("[a-zA-Z]");
//
//        String[] encontrados = pat.split(teste);
//
//        int k = 0;
//        for (String s : encontrados) {
//            System.out.println("[" + k + "] " + s);
//            k++;
//        }
//        return;
        FileInputStream stream = null;

        try {
            stream = new FileInputStream("C:\\Users\\Lucas\\Desktop\\short-abstracts_en.ttl");

        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo nao encontrado!");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        int primeiroEspaco;
        int segundoEspaco;

        int TAMANHO_TABELA = 3463179;
        TabelaHash tbHash = new TabelaHash(TAMANHO_TABELA);
//        HashSet<String> hashSet = new HashSet<>(3463179, 1);
        Scanner keyboard = new Scanner(System.in);
        int numero_de_palavras = 0;

        int numero_de_palavras_puladas = 0;

        int contaLinhas = 0;
        int porcentagem = -1;
        try {
            String linha;
            while ((linha = in.readLine()) != null) {
                if (contaLinhas % 43051 == 0) {
                    porcentagem++;
                    System.out.println(porcentagem + "% ...");

                    System.out.println("Colisoes: " + tbHash.colisoes());

//                    keyboard.next();
                }
                contaLinhas++;

                if (linha.length() < 77) {
                    System.out.println("Linha nao usada: " + linha);
                } else {

                    primeiroEspaco = linha.indexOf(' ');
                    segundoEspaco = (linha.substring(primeiroEspaco + 1)).indexOf(' ');

                    /// Seleciona o titulo
                    String titulo = linha.substring(1, primeiroEspaco - 1);

                    /// Seleciona a parte do texto entre as aspas
                    String texto = linha.substring(primeiroEspaco + segundoEspaco + 3, linha.length() - 6);

                    /// Remove acentos
                    // texto = SemAcento(texto); // Muito ruim
                    // texto = StringUtils.stripAccents(texto); // Muito ruim
                    texto = Normaliza(texto);

                    /// Remove pontuação
                    texto = texto.replaceAll("[.,/\\\\()\\[\\]:\";]", " ");

                    /// lowerCase
                    // texto = StringUtils.lowerCase(texto); //Desempenho similar
                    texto = texto.toLowerCase();

                    /// Encontra as palavras
                    String[] palavras = texto.split(" ");

                    numero_de_palavras = numero_de_palavras + palavras.length;

                    for (String s : palavras) {

                        if (s.isEmpty() || (s.replaceAll(" ", "")).isEmpty()) {
                            numero_de_palavras_puladas++;
                            continue;
                        }

//                        System.out.println("Palavra: " + s);
                        if (s.length() > 20) {
                            s = s.substring(0, 20);
                        }
                        while (s.length() < 20) {
                            s = s + ' ';
                        }

//                        System.out.println("Ajustada: " + s);
                        byte[] bb = s.getBytes();

                        int valorHash = MurmurHash.murmurHash2(bb, 541);

                        int nn = Math.abs(valorHash % (TAMANHO_TABELA));

//                        System.out.println("hash: " + nn);
//                        keyboard.next();
                        tbHash.insere(nn);
                    }
                    //hashSet.addAll(Arrays.asList(palavras));
                }

            }
        } catch (IOException ex) {
            System.out.println("IO Exception!");
        }

        //System.out.println("Tamanho do hashset: " + hashSet.size());
        System.out.println("Numero de linhas no arquivo: " + contaLinhas);
        System.out.println("Colisoes: " + tbHash.colisoes());
        System.out.println("Numero de palavras: " + numero_de_palavras);
        System.out.println("Numero de palavras puladas: " + numero_de_palavras_puladas);
        System.out.println("Numero de espaços vazios: " + tbHash.vazios());

    }

    static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String SemAcento(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);

        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    /**
     * Possui um melhor desempenho que a função "SemAcento"
     */
    public static String Normaliza(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        return string.replaceAll("\\p{M}", "");
    }

}

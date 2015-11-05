package trabalhoaed;

import Estruturas.Palavra;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas
 */
public class TrabalhoAED {

    public static void main(String[] args) {

        FileInputStream stream = null;

        try {
            stream = new FileInputStream("C:\\Users\\Lucas\\Desktop\\short-abstracts_en.ttl");

        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo nao encontrado!");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

        Scanner keyboard = new Scanner(System.in);
        String linha = null;
        long media = 0;
        int total = 0;

        long[] valores = new long[4305030];

        try {
            while ((linha = in.readLine()) != null) {
//                System.out.println(linha);
//                keyboard.next();
//                media = media + linha.length();
//                valores[total] = linha.length();
//                total++;
            }
        } catch (IOException ex) {
            System.out.println("IO Exception!");
        }

        System.out.println("Media = " + media + " / " + total + " = " + media / total);
        media = media / total;
        // A maior linha tem 42984 caracteres
        // Media de tamanho de cada linha: 395
        // Numero de linhas 4.305.030

        double somatorio = 0.0;
        for (int k = 0; k < 4305030; k++) {
            somatorio = somatorio + Math.pow((valores[k] - media), 2);
//            System.out.println("Valor: " + valores[k]);
//            System.out.println("Media: " + media);
//            System.out.println("Valor - media = " + (valores[k] - media));
//            System.out.println("(valor-media)^2 = " + Math.pow((valores[k] - media), 2));
//            System.out.println("Somatorio parcial: " + somatorio);
//            keyboard.next();

        }

        double variancia = somatorio / total;
        System.out.println("Variancia: " + variancia);

        double desvioPadrao = Math.sqrt(variancia);
        System.out.println("Desvio Padrao: " + desvioPadrao);

//        System.out.println("A maior linha tem " + maior + " caracteres.");
//        PriorityQueue<Par> fila = new PriorityQueue<>();
//
//        while (fila.size() != 0) {
//            System.out.println(fila.remove().doc_id);
//        }
    }

}

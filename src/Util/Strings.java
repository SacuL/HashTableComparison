package Util;

import java.text.Normalizer;

public class Strings {

    public static String NormalizaTexto(String texto) {

        /// Remove acentos
        // texto = StringUtils.stripAccents(texto); // Muito ruim
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("\\p{M}", "");

        /// Remove pontuação
        texto = texto.replaceAll("[.,/\\\\()\\[\\]:\";]", " ");

        /// lowerCase
        // texto = StringUtils.lowerCase(texto); //Desempenho similar
        texto = texto.toLowerCase();

        return texto;
    }

    /**
     * Normaliza a palavra (completando espaços ou reduzindo o tamanho até 20
     * caracteres) e retorna um vetor de bytes
     */
    public static byte[] BytePalavraNormalizada(String palavra) {

        if (palavra.length() > 20) {
            palavra = palavra.substring(0, 20);
        }
        while (palavra.length() < 20) {
            palavra = palavra + ' ';
        }

        return palavra.getBytes();

    }

    /**
     * Retorna a palavra normalizada (completando espaços ou reduzindo o tamanho
     * até 20 caracteres)
     */
    public static String PalavraNormalizada(String palavra) {

        if (palavra.length() > 20) {
            palavra = palavra.substring(0, 20);
        }
        while (palavra.length() < 20) {
            palavra = palavra + ' ';
        }

        return palavra;

    }

}

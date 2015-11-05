package Util;

import java.text.Normalizer;

public class Strings {

//    static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
//
//    public static String SemAcento(String str) {
//        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
//
//        return pattern.matcher(nfdNormalizedString).replaceAll("");
//    }
    public static String NormalizaTexto(String texto) {

        /// Remove acentos
        // texto = SemAcento(texto); // Muito ruim
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

}

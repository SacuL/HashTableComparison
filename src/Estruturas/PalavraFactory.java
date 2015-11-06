package Estruturas;

/**
 *
 * @author Lucas
 */
public class PalavraFactory {

    public enum TipoPalavra {

        MAP, LIST
    }

    public static InterfacePalavra criaPalavra(String texto, int id_documento, TipoPalavra tipoPalavra) {

        switch (tipoPalavra) {
            case LIST:
                return new PalavraList(texto, id_documento);

            case MAP:
                return new PalavraMap(texto, id_documento);

        }

        return null;

    }
}

package FuncoesHash;

/**
 *
 * @author Lucas
 */
public class FuncaoHashingFactory {

    public enum Funcao {

        MURMURHASHING3
    }

    public static InterfaceHashing criaHashing(Funcao funcao) {

        switch (funcao) {
            case MURMURHASHING3:
                return new MurmurHash();

        }

        return null;

    }
}

package FuncoesHash;

/**
 *
 * @author Lucas
 */
public class FuncaoHashingFactory {

    public enum Funcao {

        FNV, MURMURHASHING3, JAVA_STRING_HASH, CITYHASH, SUPERFASTHASH, CRC32
    }

    public static InterfaceHashing criaHashing(Funcao funcao) {

        switch (funcao) {
            case MURMURHASHING3:
                return new MurmurHash3A();
            case JAVA_STRING_HASH:
                return new JavaStringHash();
            case CITYHASH:
                return new CityHash();
            case SUPERFASTHASH:
                return new SuperFastHash();
            case CRC32:
                return new CRC32Hash();
            case FNV:
                return new FNV();
            default:
                return new FNV();

        }

    }
}

package FuncoesHash;

/**
 * Interface de hashing a qual todas as funções de hashing implementam
 */
public interface InterfaceHashing {

    public int hash(byte[] data, int offset, int len, int seed);
}

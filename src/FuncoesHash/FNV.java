package FuncoesHash;

public class FNV implements InterfaceHashing {

    private static final int FNV_32_INIT = 0x811c9dc5;
    private static final int FNV_32_PRIME = 0x01000193;

    @Override
    public long hashLong(String chave) {

        int rv = FNV_32_INIT;
        final int len = chave.length();
        for (int i = 0; i < len; i++) {
            rv ^= chave.charAt(i);
            rv *= FNV_32_PRIME;
        }
        return Math.abs(rv);
    }

    public int hash(String chave) {
        int rv = FNV_32_INIT;
        final int len = chave.length();
        for (int i = 0; i < len; i++) {
            rv ^= chave.charAt(i);
            rv *= FNV_32_PRIME;
        }
        return Math.abs(rv);
    }

}

package FuncoesHash;

public class SuperFastHash implements InterfaceHashing {

    public long hashLong(String s) {

        ///////////////////////////
        ////   Modificações    ////
        ///////////////////////////
        final String key = s;

        ///////////////////////////
        ///  Fim Modificações  ////
        ///////////////////////////
        int length = key.length();
        int hash = length, tmp, rem;

        rem = length & 3;
        length >>= 2;

        // Mix function, iterates through input string in 4 byte chunks.
        int i = 0;
        while (i < length) {
            hash += get16bits(key, i);
            tmp = (get16bits(key, i + 2) << 11) ^ hash;
            hash = (hash << 16) ^ tmp;
            hash += hash >> 11;
            i += 4;
        }

        switch (rem) {
            case 3:
                hash += get16bits(key, i);
                hash ^= hash << 16;
                hash ^= key.charAt(i + 1);
                hash += hash >> 11;
                break;
            case 2:
                hash += get16bits(key, i);
                hash ^= hash << 11;
                hash += hash >> 17;
                break;
            case 1:
                hash += key.charAt(i);
                hash ^= hash << 10;
                hash += hash >> 1;
        }

        /* Force "avalanching" of final 127 bits */
        hash ^= hash << 3;
        hash += hash >> 5;
        hash ^= hash << 4;
        hash += hash >> 17;
        hash ^= hash << 25;
        hash += hash >> 6;

        // Modificação no retorno.
        // Antes simplesmente retornava hash.
        return (long) hash & 0xffffffffL;
    }

    private int get16bits(final String key, final int index) {
        return (int) (key.charAt(index) | (key.charAt(index + 1) << 8));
    }

}

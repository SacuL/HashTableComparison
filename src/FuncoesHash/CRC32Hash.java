package FuncoesHash;

import java.util.zip.CRC32;

public class CRC32Hash implements InterfaceHashing {

    private final CRC32 a = new CRC32();

    @Override
    public long hashLong(String s) {

        a.update(s.getBytes());

        return a.getValue() & 0xffffffffL;

    }

    public int hash(String s) {

        a.update(s.getBytes());

        return (int) (a.getValue() & 0xffffffffL);

    }

}

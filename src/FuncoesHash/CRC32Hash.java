package FuncoesHash;

import java.util.zip.CRC32;

public class CRC32Hash implements InterfaceHashing {

    @Override
    public long hashLong(String s) {

        if (s.length() < 20) {
            s = String.format("%1$-" + 20 + "s", s);
        } else {
            s = s.substring(0, 20);
        }

        CRC32 a = new CRC32();
        a.update(s.getBytes());

        return a.getValue() & 0xffffffffL;

    }

}

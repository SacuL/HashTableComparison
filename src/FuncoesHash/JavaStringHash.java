package FuncoesHash;

public class JavaStringHash implements InterfaceHashing {

    /**
     * Returns a hash code for this string. The hash code for a {@code String}
     * object is computed as
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     * using {@code int} arithmetic, where {@code s[i]} is the
     * <i>i</i>th character of the string, {@code n} is the length of the
     * string, and {@code ^} indicates exponentiation. (The hash value of the
     * empty string is zero.)
     *
     * @return a hash code value for this object.
     */
    // Implementação do String#hashCode
//    public int hashCode() {
//        int h = hash;
//        if (h == 0 && value.length > 0) {
//            char val[] = value;
//
//            for (int i = 0; i < value.length; i++) {
//                h = 31 * h + val[i];
//            }
//            hash = h;
//        }
//        return h;
//    }
    @Override
    public long hashLong(String s) {

        ///////////////////////////
        ////   Modificações    ////
        ///////////////////////////
        if (s.length() < 20) {
            s = String.format("%1$-" + 20 + "s", s);
        } else {
            s = s.substring(0, 20);
        }
        ///////////////////////////
        ///  Fim Modificações  ////
        ///////////////////////////

        return (long) s.hashCode() & 0xffffffffL;
    }

}

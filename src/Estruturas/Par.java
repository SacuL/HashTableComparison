package Estruturas;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 *
 * @author Lucas
 */
public class Par implements Comparable {

    public int cout;
    public int doc_id;
    public String nome;

    public Par(int doc_id, String nome) {
        this.cout = 0;
        this.doc_id = doc_id;
        this.nome = nome;
    }

    @Override
    public int compareTo(Object p) {
        if (p == null) {
            System.err.println("ERRO!");
        }
        Par par = (Par) p;
        if (par.doc_id == this.doc_id) {
            return 0;
        }
        if (par.doc_id > this.doc_id) {
            return -1;
        } else if (par.doc_id < this.doc_id) {
            return 1;
        }

        System.out.println("ERRO!");
        return 0;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.doc_id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Par other = (Par) obj;
        return this.doc_id == other.doc_id;
    }

    public void incrementarUm() {
        this.cout++;
    }

}

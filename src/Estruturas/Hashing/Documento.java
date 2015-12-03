package Estruturas.Hashing;

/**
 *
 * @author Lucas
 */
public class Documento {

    private String nome;
    private int numeroDeTermosDistintos;

    public Documento(String nome) {
        this.nome = nome;
        numeroDeTermosDistintos = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getNumeroDeTermosDistintos() {
        return numeroDeTermosDistintos;
    }

    public void setNumeroDeTermosDistintos(int numeroDeTermosDistintos) {
        this.numeroDeTermosDistintos = numeroDeTermosDistintos;
    }

}

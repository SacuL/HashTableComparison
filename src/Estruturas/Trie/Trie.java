package Estruturas.Trie;

public class Trie {

    private final int TAMANHO;
    private final NoInterno cabeca;
    private final int offset;

    public Trie(int limiteInf, int limiteSup) {
        this.offset = limiteInf;
        this.TAMANHO = limiteSup - limiteInf + 1;
        this.cabeca = new NoInterno();
    }

    private interface No {
    }

    private class NoInterno implements No {

        public No[] filhos = new No[TAMANHO];

    }

    private class NoFolha implements No {

        private String sufixo;

        public NoFolha(String resto) {
            this.sufixo = resto;
        }

        public String getSufixo() {
            return sufixo;
        }

        public void setSufixo(String resto) {
            this.sufixo = resto;
        }

        public char charAt(int i) {
            return sufixo.charAt(i);
        }

    }

    // Lembrar: todas as chaves tem tamanho 20!!
    public void insere(String p) {
//        System.out.println("Inserindo chave: " + p);
        int TAMANHO_CHAVE = 20;
        No[] nos = cabeca.filhos;
        for (int i = 0; i < TAMANHO_CHAVE; i++) {
            int posicao = posicaoASCII(p.charAt(i));
//            System.out.println("Caractere '" + p.charAt(i) + "' representa a posicao " + posicao + " do vetor");
            if (nos[posicao] == null) {
//                System.out.println("Achei uma posicao vazia... inserindo o resto da palavra(" + p.substring(i + 1) + ")");
                nos[posicao] = new NoFolha(p.substring(i + 1));
                return;
            } else {
                if (nos[posicao] instanceof NoFolha) {
//                    System.out.println("Achei uma folha... inserindo o resto da palavra(" + p.substring(i + 1) + ")");
                    nos[posicao] = insereAPartirDeFolha((NoFolha) nos[posicao], p.substring(i + 1));
                    return;
                } else {
//                    System.out.println("Descendo para um no interno (" + p.charAt(i) + ")");
                    nos = ((NoInterno) nos[posicao]).filhos;
                }
            }
        }

    }

    private No insereAPartirDeFolha(NoFolha folha, String s) {

        if (folha.sufixo.equals(s)) {
            return folha;
        }

//        System.out.println(" - Inserindo o sufixo " + s + " na folha " + folha.sufixo);
        NoInterno novoNo = new NoInterno();
        int i = 0;

        NoInterno noAtual = novoNo;
        while (i < s.length()) {
            if (s.charAt(i) == folha.charAt(i)) {

//                System.out.println(" - Caracteres iguais (" + folha.charAt(i) + ")");
                int posicao = posicaoASCII(s.charAt(i));
                NoInterno novoNoInterno = new NoInterno();
                noAtual.filhos[posicao] = novoNoInterno;
                noAtual = novoNoInterno;
            } else {
//                System.out.println(" - Caracteres diferentes (" + folha.charAt(i) + "," + s.charAt(i) + ")");
                NoFolha novaFolha1 = new NoFolha(folha.getSufixo().substring(i + 1));
                NoFolha novaFolha2 = new NoFolha(s.substring(i + 1));
//                System.out.println(" - Folhas criadas: " + novaFolha1.sufixo + " e " + novaFolha2.sufixo);

                int posicao1 = posicaoASCII(folha.charAt(i));
                int posicao2 = posicaoASCII(s.charAt(i));
//                System.out.println("Posicoes dos caracteres: " + posicao1 + ", " + posicao2);
                noAtual.filhos[posicao1] = novaFolha1;
                noAtual.filhos[posicao2] = novaFolha2;
                break;
            }
            i++;
        }

        return novoNo;
    }

    private int posicaoASCII(char c) {
        return ((int) c) - offset;
    }

    private int imprimeChaves(String s, No[] nos) {
        int contador = 0;
        for (int i = 0; i < TAMANHO; i++) {
            if (nos[i] != null) {
                char charAtual = (char) (i + offset);
                if (nos[i] instanceof NoFolha) {
                    NoFolha folha = (NoFolha) nos[i];
//                    System.out.println("-> " + s + "|" + charAtual + "|" + folha.sufixo);
                    contador++;
                } else {
                    NoInterno interno = (NoInterno) nos[i];
                    contador = contador + imprimeChaves(s + charAtual, interno.filhos);
                }
            }
        }
        return contador;
    }

    public void imprimeChaves() {
        System.out.println("Há " + imprimeChaves("", this.cabeca.filhos) + " chaves na trie.");
    }
}

package Estruturas.Trie;

import Estruturas.Documento;
import Estruturas.InterfacePalavra;
import java.util.ArrayList;

public class ASCII_Trie {

    private final int TAMANHO;
    private final NoInterno raiz;
    private final ArrayList<Documento> documentos;
    private double logDoTotalDeDocumentos;

    public ASCII_Trie(int tamanho_tabela, int numero_documentos) {
        this.TAMANHO = tamanho_tabela;
        this.raiz = new NoInterno();

        if (numero_documentos < 0) {
            this.documentos = new ArrayList<>(numero_documentos);
        } else {
            this.documentos = new ArrayList<>();
        }

    }

    public void insereDocumento(Documento doc, int id_documento) {
        this.documentos.add(id_documento, doc);
    }

    public double log2NumeroTotalDeDocumentos() {
        return this.logDoTotalDeDocumentos;
    }

    public PalavraMapTrie buscarPalavra(String p) {
        int TAMANHO_CHAVE = 20;
        No[] nos = raiz.filhos;
        for (int i = 0; i < TAMANHO_CHAVE; i++) {
            int posicao = posicaoASCII(p.charAt(i));
            if (nos[posicao] == null) {
                // Palavra nao encontrada
                return null;
            } else {
                if (nos[posicao] instanceof NoFolha) {
                    // Palavra encontrada
                    return ((NoFolha) nos[posicao]).ocorrencias;
                } else {
                    nos = ((NoInterno) nos[posicao]).filhos;
                }
            }
        }
        return null;
    }

    /**
     * Retorna o número de termos distintos do documento
     */
    public int numeroTermosDistintosDocumento(int id_documento) {
        return this.documentos.get(id_documento).getNumeroDeTermosDistintos();
    }

    public Documento getDocumento(int id_documento) {
        return documentos.get(id_documento);
    }

    /////////////////////////////////////
    /////////// CLASSES DO NO ///////////
    /////////////////////////////////////
    private interface No {
    }

    private class NoInterno implements No {

        public No[] filhos = new No[TAMANHO];

    }

    private class NoFolha implements No {

        private String sufixo;
        private PalavraMapTrie ocorrencias;

        public NoFolha(String resto, int id_documento) {
            this.sufixo = resto;
            this.ocorrencias = new PalavraMapTrie(id_documento);
        }

        public char charAt(int i) {
            return sufixo.charAt(i);
        }

    }
    /////////////////////////////////////
    ///////// FIM CLASSES DO NO /////////
    /////////////////////////////////////

    // Lembrar: todas as chaves tem tamanho 20!!
    public void insere(String p, int id_documento) {
//        System.out.println("Inserindo chave: " + p);
        int TAMANHO_CHAVE = 20;
        No[] nos = raiz.filhos;
        for (int i = 0; i < TAMANHO_CHAVE; i++) {
            int posicao = posicaoASCII(p.charAt(i));
//            System.out.println("Caractere '" + p.charAt(i) + "' representa a posicao " + posicao + " do vetor");
            if (nos[posicao] == null) {
//                System.out.println("Achei uma posicao vazia... inserindo o resto da palavra(" + p.substring(i + 1) + ")");
                nos[posicao] = new NoFolha(p.substring(i + 1), id_documento);
                return;
            } else {
                if (nos[posicao] instanceof NoFolha) {
//                    System.out.println("Achei uma folha... inserindo o resto da palavra(" + p.substring(i + 1) + ")");
                    nos[posicao] = insereAPartirDeFolha((NoFolha) nos[posicao], p.substring(i + 1), id_documento);
                    return;
                } else {
//                    System.out.println("Descendo para um no interno (" + p.charAt(i) + ")");
                    nos = ((NoInterno) nos[posicao]).filhos;
                }
            }
        }

    }

    private No insereAPartirDeFolha(NoFolha folha, String s, int id_documento) {

        if (folha.sufixo.equals(s)) {
            folha.ocorrencias.insere(id_documento);
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
                NoFolha novaFolha = new NoFolha(s.substring(i + 1), id_documento);
//                System.out.println(" - Novas folhas: " + folha.sufixo + " e " + novaFolha.sufixo);

                int posicao1 = posicaoASCII(folha.charAt(i));
                int posicao2 = posicaoASCII(s.charAt(i));
//                System.out.println("Posicoes dos caracteres: " + posicao1 + ", " + posicao2);

                folha.sufixo = folha.sufixo.substring(i + 1);
                noAtual.filhos[posicao1] = folha;
                noAtual.filhos[posicao2] = novaFolha;
                break;
            }
            i++;
        }

        return novoNo;
    }

    private int posicaoASCII(char c) {
        return (tabelaASCII[(int) c]);
    }

    private int imprimeChaves(String s, No[] nos) {
        int contador = 0;
        for (int i = 0; i < TAMANHO; i++) {
            if (nos[i] != null) {
                char charAtual = (char) (tabelaASCII_volta[i]);
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
        System.out.println("Iniciando impressao...");
        System.out.println("Há " + imprimeChaves("", this.raiz.filhos) + " chaves na trie.");
    }

    private final int[] tabelaASCII = {
        -1, //0
        -1, //1
        -1, //2
        -1, //3
        -1, //4
        -1, //5
        -1, //6
        -1, //7
        -1, //8
        -1, //9
        -1, //10
        -1, //11
        -1, //12
        -1, //13
        -1, //14
        -1, //15
        -1, //16
        -1, //17
        -1, //18
        -1, //19
        -1, //20
        -1, //21
        -1, //22
        -1, //23
        -1, //24
        -1, //25
        -1, //26
        -1, //27
        -1, //28
        -1, //29
        -1, //30
        -1, //31
        0, //32
        1, //33
        -1, //34  -
        2, //35
        3, //36
        4, //37
        5, //38
        6, //39
        -1, //40 -
        -1, //41 - 
        7, //42
        8, //43
        -1, //44 -
        9, //45
        -1, //46 - 
        -1, //47 - 
        10, //48
        11, //49
        12, //50
        13, //51
        14, //52
        15, //53
        16, //54
        17, //55
        18, //56
        19, //57
        -1, //58 -
        -1, //59 -
        20, //60
        21, //61
        22, //62
        23, //63
        24, //64
        -1, //65
        -1, //66
        -1, //67
        -1, //68
        -1, //69
        -1, //70
        -1, //71
        -1, //72
        -1, //73
        -1, //74
        -1, //75
        -1, //76
        -1, //77
        -1, //78
        -1, //79
        -1, //80
        -1, //81
        -1, //82
        -1, //83
        -1, //84
        -1, //85
        -1, //86
        -1, //87
        -1, //88
        -1, //89
        -1, //90
        -1, //91 -
        -1, //92  -
        -1, //93 -
        25, //94
        26, //95
        27, //96
        28, //97
        29, //98
        30, //99
        31, //100
        32, //101
        33, //102
        34, //103
        35, //104
        36, //105
        37, //106
        38, //107
        39, //108
        40, //109
        41, //110
        42, //111
        43, //112
        44, //113
        45, //114
        46, //115
        47, //116
        48, //117
        49, //118
        50, //119
        51, //120
        52, //121
        53, //122
        54, //123
        55, //124
        56, //125
        57, //126
        -1 //127 
    };

    private final int[] tabelaASCII_volta = {
        32,
        33,
        35,
        36,
        37,
        38,
        39,
        42,
        43,
        45,
        48,
        49,
        50,
        51,
        52,
        53,
        54,
        55,
        56,
        57,
        60,
        61,
        62,
        63,
        64,
        94,
        95,
        96,
        97,
        98,
        99,
        100,
        101,
        102,
        103,
        104,
        105,
        106,
        107,
        108,
        109,
        110,
        111,
        112,
        113,
        114,
        115,
        116,
        117,
        118,
        119,
        120,
        121,
        122,
        123,
        124,
        125,
        126
    };

    public void calculaLog2NumeroDocumentos() {
        System.out.print(" -> " + this.documentos.size() + " documentos foram lidos.");
        this.logDoTotalDeDocumentos = Math.log(this.documentos.size()) / Math.log(2);
        System.out.println(" Log2(" + this.documentos.size() + ")=" + this.logDoTotalDeDocumentos);
    }

}

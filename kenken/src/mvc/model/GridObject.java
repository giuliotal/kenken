package mvc.model;

import support.Cella;
import support.MathOperation;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class GridObject {

    private final int n;
    private final int[][] griglia;

    //ogni blocco viene costruito dinamicamente tramite GUI e memorizzato nella lista
    private final LinkedList<Blocco> schemaBlocchi;

    // SOLO PER TEST: DA ELIMINARE
    public GridObject(int n, int[][] griglia) {
        this.n = n;
        this.griglia = griglia;
        this.schemaBlocchi = new LinkedList<>();
    }

    public GridObject(int n) {
        this.n = n;
        this.griglia = new int[n][n];
        this.schemaBlocchi = new LinkedList<>();
    }

    public List<Blocco> getSchemaBlocchi() {
        return Collections.unmodifiableList(schemaBlocchi);
    }

    public boolean verificaBloccoTest() {
        for(Blocco b : schemaBlocchi) {
            if(!b.verificaVincolo())
                return false;
        }
        return true;
    }

    public void creaBlocco(Cella[] celle, int risultato, MathOperation operazione) {
        Blocco b = new Blocco(celle, risultato, operazione);
        schemaBlocchi.add(b);
    }

    class Blocco {

        // ogni blocco tiene dei riferimenti alle celle della griglia di gioco che lo costituiscono (memorizzandone gli indici):
        // in questo modo le celle variano con backtracking sulla griglia, e automaticamente si aggiornano negli oggetti blocco.
        // Soltanto quando il blocco è completo si effettua un controllo sul soddisfacimento del vincolo aritmetico

        private final Cella[] celle;
        private final int vincolo;
        private final MathOperation operazione;

        public Blocco(Cella[] celle, int risultato, MathOperation operazione) {
            this.celle = celle;
            this.vincolo = risultato;
            this.operazione = operazione;
        }

        public int getSize() {
            return celle.length;
        }

        public int getVincolo() {
            return vincolo;
        }

        public MathOperation getOperazione() {
            return operazione;
        }

        private <T> void swap(T[] input, int a, int b) {
            T tmp = input[a];
            input[a] = input[b];
            input[b] = tmp;
        }

        public boolean verificaVincolo() {
            int[] indici = new int[n];
            int k = 0;

            while (k < n) {
                if (indici[k] < k) {
                    swap(celle, k % 2 == 0 ?  0: indici[k], k); //ATTENZIONE LE PERMUTAZIONI VENGONO EFFETTUATE SULL'ARRAY ORIGINALE

                    Cella cella = celle[0];
                    int res = griglia[cella.getRiga()][cella.getColonna()];
                    switch(operazione){
                        case SOMMA:
                            for(int i = 1; i < celle.length; i++) {
                                Cella c = celle[i];
                                res = res + griglia[c.getRiga()][c.getColonna()];
                                if(res == vincolo)
                                    return true;
                            }
                            break;
                        case MOLTIPLICAZIONE:
                            for(int i = 1; i < celle.length; i++) {
                                Cella c = celle[i];
                                res = res * griglia[c.getRiga()][c.getColonna()];
                                if(res == vincolo)
                                    return true;
                            }
                            break;
                        case SOTTRAZIONE:
                            for(int i = 1; i < celle.length; i++) {
                                Cella c = celle[i];
                                res = res - griglia[c.getRiga()][c.getColonna()];
                                if(res == vincolo)
                                    return true;
                            }
                            break;
                        case DIVISIONE:
                            for(int i = 1; i < celle.length; i++) {
                                Cella c = celle[i];
                                res = res / griglia[c.getRiga()][c.getColonna()];
                                if(res == vincolo)
                                    return true;
                            }
                            break;
                    }

                    indici[k]++;
                    k = 0;
                }
                else {
                    indici[k] = 0;int[] indexes = new int[n];
                    k++;
                }
            }

            return false;
        }

    }

    public static void main(String[] args) {
        int[][] griglia = {{1,2,3},{4,5,6},{7,8,9}};
        GridObject g = new GridObject(griglia.length, griglia);
        Cella c1 = new Cella(0,0); //1
        Cella c2 = new Cella(1,0); //4
        Cella c3 = new Cella(2,0); //7
        Cella[] celle = {c1,c2,c3};
        g.creaBlocco(celle,2, MathOperation.SOTTRAZIONE);
        System.out.println(g.verificaBloccoTest());
    }

}

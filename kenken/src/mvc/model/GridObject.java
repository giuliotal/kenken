package mvc.model;

import java.util.*;

public class GridObject extends AbstractGridObject {

    private final int n;
    private final int[][] griglia;

    // Ogni blocco viene costruito dinamicamente e memorizzato nella lista
    private final LinkedList<Blocco> schemaBlocchi;

    // Inizializzazione manuale della griglia
    public GridObject(int n, int[][] griglia) {
        this.n = n;
        this.griglia = griglia;
        this.schemaBlocchi = new LinkedList<>();
    }

    // Inizializzazione di una griglia vuota
    public GridObject(int n) {
        this.n = n;
        this.griglia = new int[n][n];
        this.schemaBlocchi = new LinkedList<>();
    }

    public List<Blocco> getSchemaBlocchi() {
        return Collections.unmodifiableList(schemaBlocchi);
    }

    public void inserisciNumero(int numero, int riga, int colonna) {
        griglia[riga][colonna] = numero;
    }

    public void eliminaNumero(int riga, int colonna) {
        griglia[riga][colonna] = 0;
    }

    // TODO O(n^3): da rivedere
    public List<Cella> verificaDuplicati() {
        Set<Cella> celleDuplicate = new HashSet<>();
        for (int riga = 0; riga < n; riga++) {
            for (int colonna = 0; colonna < n; colonna++) {
                int valore = griglia[riga][colonna];
                // verifico i duplicati soltanto per i numeri inseriti dall'utente (che sono diversi da 0)
                for(int k = 0; k < n && valore != 0; k++){
                    if(griglia[riga][k]==valore && k!=colonna){
                        celleDuplicate.add(new Cella(riga, colonna));
                        celleDuplicate.add(new Cella(riga, k));
                    }
                    else if (griglia[k][colonna]==valore && k!=riga){
                        celleDuplicate.add(new Cella(riga, colonna));
                        celleDuplicate.add(new Cella(k, colonna));
                    }
                }
            }
        }
        return new LinkedList<>(celleDuplicate);
    }

    public List<Blocco> verificaBlocchi() {
        List<Blocco> blocchiNonCorretti = new LinkedList<>();
        for(Blocco b : schemaBlocchi) {
            if(!b.verificaVincolo())
                blocchiNonCorretti.add(b);
        }
        return blocchiNonCorretti;
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

        public List<Cella> getCelle() {return Arrays.asList(celle);}

        public int getSize() {return celle.length;}

        public int getVincolo() {return vincolo;}

        public MathOperation getOperazione() {return operazione;}

        private <T> void swap(T[] a, int i, int j) {
            T tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }

        public boolean verificaVincolo() {
           return verificaVincoloConPermutazioni(0);
        }

        /*
         *  Effettua ricorsivamente delle permutazioni sull'array di celle per verificare il soddisfacimento del vincolo
         *  aritmetico, che dipende dall'ordine degli operandi
         */
        private boolean verificaVincoloConPermutazioni(int i) {
            boolean trovato = false;

            if(i==celle.length){
                Cella cella = celle[0];
                int res = griglia[cella.getRiga()][cella.getColonna()];
                for(int k = 1; k < celle.length; k++){
                    Cella c = celle[k];
                    switch(operazione) {
                        case SOMMA:
                            res = res + griglia[c.getRiga()][c.getColonna()]; break;
                        case MOLTIPLICAZIONE:
                            res = res * griglia[c.getRiga()][c.getColonna()]; break;
                        case SOTTRAZIONE:
                            res = res - griglia[c.getRiga()][c.getColonna()]; break;
                        case DIVISIONE:
                            res = res / griglia[c.getRiga()][c.getColonna()]; break;
                    }
                }
                return res == vincolo;
            }
            else {
                for(int j=i; j<celle.length; j++){
                    swap(celle,i,j);
                    trovato = verificaVincoloConPermutazioni(i+1);
                    swap(celle,i,j);
                }
            }
            return trovato;
        }

        public String toString() {
            return Arrays.deepToString(celle);
        }

    }

    public static void main(String[] args) {
        int[][] griglia = {{1,2,3,4},{5,6,7,8},{5,10,11,12}};
        GridObject g = new GridObject(griglia.length, griglia);
        Cella c1 = new Cella(0,0); //1
        Cella c2 = new Cella(1,0); //5
        Cella c3 = new Cella(2,0); //9
        Cella[] celle = {c1,c2,c3};
        g.creaBlocco(celle,4, MathOperation.SOTTRAZIONE);
        System.out.print("Blocchi che non soddisfano il vincolo: ");
        System.out.println(g.verificaBlocchi());
        System.out.print("Celle che non soddisfano il vincolo: ");
        System.out.println(g.verificaDuplicati());
    }

}

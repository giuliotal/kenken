package mvc.model;

import java.util.*;

// TODO singleton? Avrebbe senso in quanto voglio che ci sia una sola griglia su schermo, quindi mi basta una sola istsanza
// (MODEL)
public class Grid extends AbstractGrid {

    private int n;
    private int[][] grid;

    // Ogni blocco viene costruito dinamicamente e memorizzato nella lista
    private LinkedList<Cage> cageSchema = new LinkedList<>();

    // Inizializzazione di una griglia vuota
    public Grid() {
        this.n = 0;
        this.grid = new int[0][0];
    }
    
    // Inizializzazione manuale della griglia
    public Grid(int n, int[][] griglia) {
        this.n = n;
        this.grid = griglia;
    }

    public int getSize() { return n; }

    public List<Cage> getCageSchema() {
        return Collections.unmodifiableList(cageSchema);
    }

    // cambiare la dimensione significa creare una nuova griglia
    public void setSize(int n) {
        this.n = n;
        this.grid = new int[n][n];
        cageSchema.clear();
        notifyListeners(new GridEvent(this));
    }

    public void insertNumber(int number, int row, int column) {
        grid[row][column] = number;
        notifyListeners(new GridEvent(this));
    }

    public void deleteNumber(int row, int column) {
        grid[row][column] = 0;
        notifyListeners(new GridEvent(this));
    }

    // TODO O(n^3): da rivedere
    public List<Square> findDuplicates() {
        Set<Square> duplicateSquares = new HashSet<>();
        for (int row = 0; row < n; row++) {
            for (int column = 0; column < n; column++) {
                int val = grid[row][column];
                // verifico i duplicati soltanto per i numeri inseriti dall'utente (che sono diversi da 0)
                for(int k = 0; k < n && val != 0; k++){
                    if(grid[row][k]==val && k!=column){
                        duplicateSquares.add(new Square(row, column));
                        duplicateSquares.add(new Square(row, k));
                    }
                    else if (grid[k][column]==val && k!=row){
                        duplicateSquares.add(new Square(row, column));
                        duplicateSquares.add(new Square(k, column));
                    }
                }
            }
        }
        return new LinkedList<>(duplicateSquares);
    }

    public List<Cage> findIncorrectCages() {
        List<Cage> incorrectCages = new LinkedList<>();
        for(Cage b : cageSchema) {
            if(!b.verifyCage())
                incorrectCages.add(b);
        }
        return incorrectCages;
    }

    public void createCage(Square[] s, int result, MathOperation op) {
        Cage b = new Cage(s, result, op);
        cageSchema.add(b);
        //notifyListeners(new GridEvent(this));
    }

    class Cage {

        // ogni blocco tiene dei riferimenti alle celle della griglia di gioco che lo costituiscono (memorizzandone gli indici):
        // in questo modo le celle variano con backtracking sulla griglia, e automaticamente si aggiornano negli oggetti blocco.
        // Soltanto quando il blocco è completo si effettua un controllo sul soddisfacimento del vincolo aritmetico

        private final Square[] squares;
        private final int result;
        private final MathOperation operation;

        public Cage(Square[] squares, int result, MathOperation operation) {
            this.squares = squares;
            this.result = result;
            this.operation = operation;
        }

        public List<Square> getCells() {return Arrays.asList(squares);}

        public int getSize() {return squares.length;}

        public int getResult() {return result;}

        public MathOperation getOperation() {return operation;}

        private <T> void swap(T[] a, int i, int j) {
            T tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }

        public boolean verifyCage() {
           return verifyCellsCombinations(0);
        }

        /*
         *  Effettua ricorsivamente delle permutazioni sull'array di celle per verificare il soddisfacimento del vincolo
         *  aritmetico, che dipende dall'ordine degli operandi
         */
        private boolean verifyCellsCombinations(int i) {
            boolean verified = false;

            if(i== squares.length){
                Square square = squares[0];
                int res = grid[square.getRow()][square.getColumn()];
                for(int k = 1; k < squares.length; k++){
                    Square c = squares[k];
                    switch(operation) {
                        case SUM:
                            res = res + grid[c.getRow()][c.getColumn()]; break;
                        case MULTIPLICATION:
                            res = res * grid[c.getRow()][c.getColumn()]; break;
                        case SUBTRACTION:
                            res = res - grid[c.getRow()][c.getColumn()]; break;
                        case DIVISION:
                            res = res / grid[c.getRow()][c.getColumn()]; break;
                    }
                }
                return res == result;
            }
            else {
                for(int j = i; j< squares.length; j++){
                    swap(squares,i,j);
                    verified = verifyCellsCombinations(i+1);
                    swap(squares,i,j);
                }
            }
            return verified;
        }

        public String toString() {
            return Arrays.deepToString(squares);
        }

    }

    public static void main(String[] args) {
        int[][] griglia = {{1,2,3,4},{5,6,7,8},{5,10,11,12}};
        Grid g = new Grid(griglia.length, griglia);
        Square c1 = new Square(0,0); //1
        Square c2 = new Square(1,0); //5
        Square c3 = new Square(2,0); //9
        Square[] celle = {c1,c2,c3};
        g.createCage(celle,4, MathOperation.SUBTRACTION);
        System.out.print("Blocchi che non soddisfano il vincolo: ");
        System.out.println(g.findIncorrectCages());
        System.out.print("Celle che non soddisfano il vincolo: ");
        System.out.println(g.findDuplicates());
    }

}

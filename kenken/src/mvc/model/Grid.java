package mvc.model;

import java.util.*;

// TODO singleton? Avrebbe senso in quanto voglio che ci sia una sola griglia su schermo, quindi mi basta una sola istsanza
// (MODEL)
public class Grid extends AbstractGrid {

    private int n;
    private int[][] grid;
    private boolean[][] selectedSquares;
    private int lockedSquares;

    // Ogni blocco viene costruito dinamicamente e memorizzato nella lista
    private final LinkedList<Cage> cageSchema = new LinkedList<>();

    // Inizializzazione di una griglia vuota
    public Grid() {
        this.n = 0;
        this.grid = new int[0][0];
        this.selectedSquares = new boolean[0][0];
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

    public boolean[][] getSelectedSquares() {
        return selectedSquares;
    }

    public int getLockedSquares() { return lockedSquares; }

    // cambiare la dimensione significa creare una nuova griglia
    public void setSize(int n) {
        this.n = n;
        this.grid = new int[n][n];
        this.selectedSquares = new boolean[n][n];
        cageSchema.clear();
        lockedSquares = 0;
        notifyListeners(new GridEvent.Builder(this).newGrid(true).build());
    }

    public void insertNumber(int number, int row, int column) {
        grid[row][column] = number;
        System.out.println(Arrays.deepToString(grid));
        notifyListeners(new GridEvent.Builder(this).userInteracted(true).build());
    }

    public void deleteNumber(int row, int column) {
        grid[row][column] = 0;
//        notifyListeners(new GridEvent.Builder(this).build());
    }

    public void clear() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = 0;
            }
        }
        notifyListeners(new GridEvent.Builder(this).cageCleared(true).build());
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
            if(!b.checkTargetResult())
                incorrectCages.add(b);
        }
        return incorrectCages;
    }

    public void checkConstraints() {
        List<Square> duplicateSquares = findDuplicates();
        List<Square> invalidTargetResult = new LinkedList<>();
        // individuo soltanto le celle contententi il target result non soddisfatto e rendo quest'ultimo rosso, non l'intero blocco
        for(Cage c : findIncorrectCages()){
            int minRow = Integer.MAX_VALUE;
            int minCol = Integer.MAX_VALUE;
            Square targetResultSquare = null;
            for(Square s : c.getSquares()) {
                int i = s.getRow();
                int j = s.getColumn();
                if(i < minRow && j < minCol) {
                    minRow = i;
                    minCol = j;
                    targetResultSquare = s;
                }
            }
            invalidTargetResult.add(targetResultSquare);
        }
        notifyListeners(new GridEvent.Builder(this).constraintsChecked(true).duplicateSquares(duplicateSquares).invalidTargetResultSquares(invalidTargetResult).build());
    }

    public void selectSquare(int i, int j) {
        selectedSquares[i][j] = !selectedSquares[i][j];
        notifyListeners(new GridEvent.Builder(this).squareSelected(true).selectedSquare(new Square(i,j)).build());
    }

    public void createCage(int result, MathOperation op) {
        LinkedList<Square> cage = new LinkedList<>();
        boolean[][] selectionSnapshot = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(selectedSquares[i][j]) {
                    cage.add(new Square(i, j));
                    selectionSnapshot[i][j] = true;
                    lockedSquares++;
                }
            }
        }
        Cage c = new Cage(cage.toArray(new Square[cage.size()]), result, op);
        cageSchema.add(c);
        notifyListeners(new GridEvent.Builder(this).cageCreated(true).selectionSnapshot(selectionSnapshot).operation(op).result(result).build());
        selectedSquares = new boolean[n][n]; //azzera la selezione corrente
    }

    public boolean isSelectionEmpty() {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (selectedSquares[i][j])
                    return false;
        return true;
    }

    public boolean verifyAdjacency() {
        boolean[][] selectionSnapshot = new boolean[n][n];
        int selectionSize = 0;
        int startRow = 0;
        int startColumn = 0;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (selectedSquares[i][j]) {
                    selectionSize++;
                    selectionSnapshot[i][j] = true;
                    startRow = i;
                    startColumn = j;
                }

        int i = startRow;
        int j = startColumn;

        return verifyAdjacency(i,j,selectionSize-1, selectionSnapshot) == 0;
    }

    private int verifyAdjacency(int i, int j, int remaining, boolean[][] squares) {
        if(i > 0 && squares[i-1][j]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i-1,j,remaining-1,squares); // up
        }
        if(i < squares.length-1 && squares[i+1][j]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i+1,j,remaining-1,squares); // down
        }
        if(j > 0 && squares[i][j-1]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i,j-1,remaining-1,squares); // left
        }
        if(j < squares.length-1 && squares[i][j+1]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i,j+1,remaining-1,squares); //right
        }
        squares[i][j] = false;
        return remaining;
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

        public List<Square> getSquares() {return Arrays.asList(squares);}

        public int getSize() {return squares.length;}

        public int getResult() {return result;}

        public MathOperation getOperation() {return operation;}

        private <T> void swap(T[] a, int i, int j) {
            T tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }

        public boolean checkTargetResult() {
           for(Square s : squares) {
               if(grid[s.getRow()][s.getColumn()] == 0) //vuol dire che il blocco non è stato riempito completamente
                   return true;
           }
           return checkTargetResult(0);
        }

        /*
         *  Effettua ricorsivamente delle permutazioni sull'array di celle per verificare il soddisfacimento del vincolo
         *  aritmetico, che dipende dall'ordine degli operandi
         */
        private boolean checkTargetResult(int i) {
            boolean verified = false;

            if(i == squares.length){
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
                    verified = checkTargetResult(i+1);
                    swap(squares,i,j);
                }
            }
            return verified;
        }

        public String toString() {
            return Arrays.deepToString(squares);
        }

    }

}

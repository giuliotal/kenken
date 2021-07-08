package mvc.model;

import java.io.*;
import java.util.*;

// (MODEL)
public class Grid extends AbstractGrid implements Serializable {

    private static final long serialVersionUID = 2581289391319703541L;

    private int n;
    private Square[][] squares;

    // memorizzo le soluzioni per poterle visualizzare individualmente
    private final ArrayList<int[][]> solutions = new ArrayList<>();
    private int[][] currentSolution;

    // Ogni blocco viene costruito dinamicamente e memorizzato nella lista
    private LinkedList<Cage> cageSchema = new LinkedList<>();

    private List<Square> duplicateSquares;

    public List<Square> getDuplicateSquares() {
        return duplicateSquares;
    }

    private List<Cage> invalidTargetResultCages;

    public List<Cage> getInvalidTargetResultCages() {
        return invalidTargetResultCages;
    }


    // Inizializzazione di una griglia vuota
    public Grid() {}

    public int getSize() { return n; }

    public int[][] getGrid() {
        int[][] ret = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ret[i][j] = squares[i][j].value;
            }
        }
        return ret;
    }

    public LinkedList<Cage> getCageSchema() {
        return cageSchema;
    }

    public int[][] getCurrentSolution() { return currentSolution; }

    // cambiare la dimensione della griglia di gioco implica la creazione di una nuova griglia vuota
    public void setSize(int n) {
        this.n = n;
        this.squares = new Square[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                squares[i][j] = new Square(i,j,0);
            }
        }
        cageSchema.clear();
        solutions.clear();
        notifyListeners(new GridEvent.Builder(this).newGrid(true).build());
    }

    public void insertNumber(int number, int row, int column) {
        squares[row][column].value = number;
        notifyListeners(new GridEvent.Builder(this).numberInserted(true).build());
    }

    public void deleteNumber(int row, int column) {
        squares[row][column].value = 0;
        notifyListeners(new GridEvent.Builder(this).numberDeleted(true).build());
    }

    public void clear() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                squares[i][j].value = 0;
            }
        }
        notifyListeners(new GridEvent.Builder(this).cageCleared(true).build());
    }

    // TODO O(n^3): da rivedere
    private List<Square> findDuplicates() {
        Set<Square> duplicateSquares = new HashSet<>();
        for (int row = 0; row < n; row++) {
            for (int column = 0; column < n; column++) {
                int val = squares[row][column].value;
                // verifico i duplicati soltanto per i numeri inseriti dall'utente (che sono diversi da 0)
                for(int k = 0; k < n && val != 0; k++){
                    if(squares[row][k].value==val && k!=column){
                        duplicateSquares.add(new Square(row, column));
                        duplicateSquares.add(new Square(row, k));
                    }
                    else if (squares[k][column].value==val && k!=row){
                        duplicateSquares.add(new Square(row, column));
                        duplicateSquares.add(new Square(k, column));
                    }
                }
            }
        }
        return new LinkedList<>(duplicateSquares);
    }

    private List<Cage> findIncorrectCages() {
        List<Cage> incorrectCages = new LinkedList<>();
        for(Cage b : cageSchema) {
            if(!b.checkTargetResult())
                incorrectCages.add(b);
        }
        return incorrectCages;
    }

    public boolean checkConstraints() {
        List<Square> duplicateSquares = findDuplicates();
        List<Cage> invalidTargetResultCages = findIncorrectCages();

        if(duplicateSquares.isEmpty() && invalidTargetResultCages.isEmpty()) return true;

        this.duplicateSquares = duplicateSquares;
        this.invalidTargetResultCages = invalidTargetResultCages;
        notifyListeners(new GridEvent.Builder(this).constraintsChecked(true).build());
        return false;
    }

    public void findSolutions(int maxSolutions) {
        // effettuo il calcolo delle soluzioni solo se non ho già a disposizione il numero di soluzioni richiesto
        if(solutions.size() != maxSolutions) {
            clear();
            solutions.clear();
            new KenkenSolutions().risolvi(maxSolutions);
        }
        if(solutions.size() > 0) currentSolution = solutions.get(0);
        else currentSolution = null;
        notifyListeners(new GridEvent.Builder(this).solutionRequested(true).build());
    }

    public void nextSolution() {
        int i = solutions.indexOf(currentSolution);
        if(hasNextSolution()) {
            currentSolution = solutions.get(i + 1);
            notifyListeners(new GridEvent.Builder(this).solutionRequested(true).build());
        }
    }

    public boolean hasNextSolution() {
        int i = solutions.indexOf(currentSolution);
        return i + 1 <= solutions.size()-1;
    }

    public void previousSolution() {
        int i = solutions.indexOf(currentSolution);
        if(hasPreviousSolution()) {
            currentSolution = solutions.get(i - 1);
            notifyListeners(new GridEvent.Builder(this).solutionRequested(true).build());
        }
    }

    public boolean hasPreviousSolution() {
        int i = solutions.indexOf(currentSolution);
        return i - 1 >= 0;
    }

    public boolean save(String pathName) {
        if(!pathName.contains(".kenken")) return false;
        if(n == 0) return false;
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(pathName));
            oos.writeObject(squares);
            oos.writeObject(cageSchema);
            oos.close();
        }catch(IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean load(String pathName) {
        if(!pathName.contains(".kenken")) return false;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathName));
            Square[][] savedGrid = (Square[][]) ois.readObject();
            setSize(savedGrid.length);
            squares = savedGrid;
            cageSchema = (LinkedList<Cage>) ois.readObject();
            ois.close();
            notifyListeners(new GridEvent.Builder(this).newGrid(true).schemaUpdated(true).build());
        }catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Memento getMemento() {
        return new GridMemento();
    }

    @Override
    public void setMemento(Memento m) {
        if(!(m instanceof GridMemento))
            throw new IllegalArgumentException();
        GridMemento memento = (GridMemento) m;
        if(this != memento.getOriginator())
            throw new IllegalArgumentException();
        int previousSize = memento.squares.length;
        if(n != previousSize) setSize(n);
        //TODO trovare una spiegazione a questa cosa: il clone mi da problemi di aliasing
//        squares = memento.squares.clone();
//        cageSchema = (LinkedList<Cage>) memento.cageSchema.clone();
        this.cageSchema = new LinkedList<>();
        for(Cage cage : memento.cageSchema) {
            LinkedList<Square> squareList = new LinkedList<>();
            for (Square s : cage.squares) {
                squares[s.getRow()][s.getColumn()].value = s.value;
                squareList.add(s);
            }
            cageSchema.add(new Cage(squareList.toArray(new Square[squareList.size()]), cage.result, cage.operation));
        }
        notifyListeners(new GridEvent.Builder(this).schemaUpdated(true).build());
    }

    public boolean createCage(boolean[][] selectedSquares, int result, MathOperation op) {
        if(!verifyAdjacency(selectedSquares)) return false;
        LinkedList<Square> cage = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(selectedSquares[i][j]) {
                    cage.add(squares[i][j]);
                }
            }
        }
        Cage c = new Cage(cage.toArray(new Square[cage.size()]), result, op);
        cageSchema.add(c);
        notifyListeners(new GridEvent.Builder(this).schemaUpdated(true).build());
        return true;
    }

    public boolean verifyAdjacency(boolean[][] selectedSquares) {
        int n = selectedSquares.length;
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

        return verifyAdjacency(startRow,startColumn,selectionSize-1, selectionSnapshot) == 0;
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

    public static class Cage implements Serializable, Cloneable {

        private static final long serialVersionUID = -328720550746986579L;

        // ogni blocco dello schema di gioco (Cage) tiene dei riferimenti alle celle della griglia di gioco che lo costituiscono
        // Soltanto quando il blocco è completo si effettua un controllo sul soddisfacimento del vincolo aritmetico

        private Square[] squares;
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
                if(s.getValue() == 0)
                    //vuol dire che il blocco non è stato riempito completamente
                    // restituisco true perché se il blocco non è completo non ho informazioni a sufficienza per dire che il vincolo non è rispettato
                    // per cui posso dire che il blocco è ottimisticamente corretto
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
                Square firstSquare = squares[0];
                int res = firstSquare.getValue();
                for(int k = 1; k < squares.length; k++){
                    Square square = squares[k];
                    switch(operation) {
                        case SUM:
                            res = res + square.getValue(); break;
                        case MULTIPLICATION:
                            res = res * square.getValue(); break;
                        case SUBTRACTION:
                            res = res - square.getValue(); break;
                        case DIVISION:
                            res = res / square.getValue(); break;
                    }
                }
                return res == result;
            }
            else {
                // continua a permutare solo se non si è ottenuto il target result
                for(int j = i; j < squares.length && !verified; j++){
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

        @Override
        public Cage clone() {
            try {
                Cage clone = (Cage) super.clone();
                clone.squares = squares.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new Error(e);
            }
        }
    }

    public static class Square implements Serializable, Cloneable {

        private static final long serialVersionUID = -1403355865754859882L;

        private final int row;
        private final int column;
        private int value;

        public Square(int row, int column, int value) {
            this.row = row;
            this.column = column;
            this.value = value;
        }

        public Square(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public Square(Square square) {
            this.row = square.row;
            this.column = square.column;
            this.value = square.value;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public int getValue() { return value; }

        @Override
        public String toString() {
            return "<"+ row +","+ column +">";
        }

        @Override
        public boolean equals(Object o){
            if(o == this) return true;
            if(! (o instanceof Square)) return false;
            Square c = (Square)o;
            return c.row ==this.row && c.column ==this.column;
        }

        @Override
        public int hashCode() {
            final int M = 17;
            int h = 1;
            h = h * row + M;
            h = h * column + M;
            return h;
        }

        public Square clone() {
            try {
                Square clone = (Square) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new Error(e);
            }

        }

    }

    public enum MathOperation {
        SUM,
        SUBTRACTION,
        MULTIPLICATION,
        DIVISION
    }

    private class KenkenSolutions implements Backtracking<Square, Integer> {

        @Override
        public Square primoPuntoDiScelta() {
            return new Square(0,0);
        }

        @Override
        public Square prossimoPuntoDiScelta(Square ps) {
            int row = ps.getRow();
            int column = ps.getColumn();
            if(column < n-1) column++;
            else if(column == n-1) {
                row++;
                column = 0;
            }
            return new Square(row,column);
        }

        @Override
        public Square ultimoPuntoDiScelta() {
            return new Square(n-1,n-1);
        }

        @Override
        public Integer primaScelta(Square ps) {
            return 1;
        }

        @Override
        public Integer prossimaScelta(Integer integer) {
            return integer+1;
        }

        @Override
        public Integer ultimaScelta(Square ps) {
            return n;
        }

        @Override
        public boolean assegnabile(Integer scelta, Square puntoDiScelta) {
            int i = puntoDiScelta.getRow();
            int j = puntoDiScelta.getColumn();
            int tmp = squares[i][j].value; // memorizzo la scelta corrente
            squares[i][j].value = scelta; // assegno temporaneamente la nuova scelta per vedere se rispetta i vincoli
            boolean vincoliOk = true;
            List<Square> duplicateSquares = findDuplicates();
            if(!duplicateSquares.isEmpty()) vincoliOk = false;
            if(vincoliOk) {
                List<Cage> invalidTargetResult = findIncorrectCages(); // TODO ad ogni assegnamento verifico tutti i blocchi: TROPPO COSTOSO
                if (!invalidTargetResult.isEmpty()) vincoliOk = false;
            }
            if(!vincoliOk) {
                squares[i][j].value = tmp; // ripristino la vecchia griglia
                return false;
            }
            return true;
        }

        @Override
        public void assegna(Integer scelta, Square puntoDiScelta) {
            int i = puntoDiScelta.getRow();
            int j = puntoDiScelta.getColumn();
            squares[i][j].value = scelta;
        }

        @Override
        public void deassegna(Integer scelta, Square puntoDiScelta) {
            int i = puntoDiScelta.getRow();
            int j = puntoDiScelta.getColumn();
            squares[i][j].value = 0;
        }

        @Override
        public Square precedentePuntoDiScelta(Square puntoDiScelta) {
            int row = puntoDiScelta.getRow();
            int column = puntoDiScelta.getColumn();
            if(column > 0) column--;
            else if(column == 0) {
                row--;
                column = n-1;
            }
            return new Square(row,column);
        }

        @Override
        public Integer ultimaSceltaAssegnataA(Square puntoDiScelta) {
            int row = puntoDiScelta.getRow();
            int column = puntoDiScelta.getColumn();
            return squares[row][column].value;
        }

        @Override
        public void scriviSoluzione(int nr_sol) {
            int[][] gridCopy = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    gridCopy[i][j] = squares[i][j].value;
                }
            }
            solutions.add(gridCopy);
        }
    }

    private class GridMemento implements Memento {

        final Square[][] squares;
        final LinkedList<Cage> cageSchema;

        Grid getOriginator() { return Grid.this; }

        GridMemento() {
            squares = new Square[n][n];
            this.cageSchema = new LinkedList<>();
            for(Cage cage : Grid.this.cageSchema) {
                LinkedList<Square> squareList = new LinkedList<>();
                for (Square square : cage.squares) {
                    Square s = new Square(square);
                    squares[s.getRow()][s.getColumn()] = s;
                    squareList.add(s);
                }
                cageSchema.add(new Cage(squareList.toArray(new Square[squareList.size()]), cage.result, cage.operation));
            }
        }

        public String toString() {
            return cageSchema.toString();
        }

    }

}

package mvc.model;

import mvc.model.exceptions.SolutionsNotFoundException;

import java.io.*;
import java.util.*;

// (MODEL)
public class Grid extends AbstractGrid implements Serializable {

    private static final long serialVersionUID = 2581289391319703541L;

    private int n;
    private Square[][] squares;

    // Le soluzioni calcolate vengono memorizzate per essere visualizzate individualmente
    private final ArrayList<int[][]> solutions = new ArrayList<>();
    private int currentSolution = 0;

    // Ogni blocco dello schema di gioco viene memorizzato
    private LinkedList<Cage> cageSchema = new LinkedList<>();

    // Contiene le celle i cui valori risultano essere duplicati sulla stessa riga o colonna
    private List<Square> duplicateSquares;

    // Contiene i blocchi dello schema che non rispettano il vincolo aritmetico
    private List<Cage> invalidTargetResultCages;

    // La griglia di gioco viene inizializzata vuota
    public Grid() {}

    public int getSize() { return n; }

    public Square[][] getGrid() {
        return squares;
    }

    public List<Cage> getCageSchema() {
        return Collections.unmodifiableList(cageSchema);
    }

    public int[][] getCurrentSolution() throws SolutionsNotFoundException {
        if(solutions.size() > 0) return solutions.get(currentSolution);
        throw new SolutionsNotFoundException();
    }

    public List<Square> getDuplicateSquares() { return duplicateSquares; }

    public List<Cage> getInvalidTargetResultCages() { return invalidTargetResultCages; }

    // cambiare la dimensione della griglia di gioco implica la creazione di una nuova griglia vuota
    public void setSize(int n) {
        if(n < 3) throw new IllegalArgumentException("Size must be at least 3 for a Kenken game");
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
        if(number < 0 || number > n) throw new IllegalArgumentException("Invalid number: " + number);
        if(row < 0 || row > n-1) throw new IllegalArgumentException("Invalid row: " + row);
        if(column < 0 || column > n-1) throw new IllegalArgumentException("Invalid column: " + column);
        squares[row][column].setValue(number);
        notifyListeners(new GridEvent.Builder(this).numberInserted(true).build());
    }

    public void deleteNumber(int row, int column) {
        if(row < 0 || row > n-1) throw new IllegalArgumentException("Invalid row");
        if(column < 0 || column > n-1) throw new IllegalArgumentException("Invalid column");
        squares[row][column].setValue(0);
        notifyListeners(new GridEvent.Builder(this).numberInserted(true).build());
    }

    public void clear() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                squares[i][j].setValue(0);
            }
        }
        notifyListeners(new GridEvent.Builder(this).cageCleared(true).build());
    }

    public boolean createCage(Square[] squares, int result, MathOperation op) {
        if(squares.length == 0) throw new IllegalArgumentException();
        boolean[][] selectedSquares = new boolean[n][n];
        for(Square s : squares) selectedSquares[s.getRow()][s.getColumn()] = true;
        if(!Cage.verifyAdjacency(selectedSquares)) throw new IllegalArgumentException("Invalid square selection");

        Square[] localSquares = new Square[squares.length];
        for (int i = 0; i < squares.length; i++) {
            Square s = squares[i];
            localSquares[i] = this.squares[s.getRow()][s.getColumn()];
        }
        Cage c = new Cage(localSquares, result, op);
        cageSchema.add(c);
        notifyListeners(new GridEvent.Builder(this).schemaUpdated(true).build());
        return true;
    }

    private List<Square> findDuplicates() {
        Set<Square> duplicateSquares = new HashSet<>();
        for (int row = 0; row < n; row++) {
            for (int column = 0; column < n; column++) {
                int val = squares[row][column].getValue();
                // verifico i duplicati soltanto per i numeri validi (che sono diversi da 0)
                for(int k = 0; k < n && val != 0; k++){
                    if(squares[row][k].getValue()==val && k!=column){
                        duplicateSquares.add(new Square(row, column));
                        duplicateSquares.add(new Square(row, k));
                    }
                    else if (squares[k][column].getValue()==val && k!=row){
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

    public void findSolutions(int maxSolutions) throws SolutionsNotFoundException {
        // effettuo il calcolo delle soluzioni solo se non ho già a disposizione il numero di soluzioni richiesto
        if(solutions.size() != maxSolutions) {
            clear();
            solutions.clear();
            new KenkenSolutions().solve(maxSolutions);
        }
        if(solutions.isEmpty()) throw new SolutionsNotFoundException();
        currentSolution = 0;
        notifyListeners(new GridEvent.Builder(this).solutionRequested(true).build());
    }

    public int getTotalSolutions() {
        return solutions.size();
    }

    public void nextSolution() {
        if(hasNextSolution()) {
            currentSolution++;
            notifyListeners(new GridEvent.Builder(this).solutionRequested(true).build());
        }
    }

    public boolean hasNextSolution() {
        return currentSolution + 1 <= solutions.size()-1;
    }

    public void previousSolution() {
        if(hasPreviousSolution()) {
            currentSolution--;
            notifyListeners(new GridEvent.Builder(this).solutionRequested(true).build());
        }
    }

    public boolean hasPreviousSolution() {
        return currentSolution - 1 >= 0;
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
        this.cageSchema = new LinkedList<>();
        for(Cage cage : memento.cageSchema) {
            LinkedList<Square> squareList = new LinkedList<>();
            for (Square s : cage.getSquares()) {
                Square square = squares[s.getRow()][s.getColumn()];
                square.setValue(s.getValue());
                squareList.add(square);
            }
            cageSchema.add(new Cage(squareList.toArray(new Square[0]), cage.getResult(), cage.getOperation()));
        }
        notifyListeners(new GridEvent.Builder(this).schemaUpdated(true).build());
    }

    private class KenkenSolutions implements Backtracking<Square, Integer> {

        @Override
        public Square firstDecisonPoint() {
            return squares[0][0];
        }

        @Override
        public Square nextDecisionPoint(Square point) {
            int row = point.getRow();
            int column = point.getColumn();
            if(column < n-1) column++;
            else if(column == n-1) {
                row++;
                column = 0;
            }
            return squares[row][column];
        }

        @Override
        public Square lastDecisionPoint() {
            return squares[n-1][n-1];
        }

        @Override
        public Integer firstChoice(Square point) {
            return 1;
        }

        @Override
        public Integer nextChoice(Integer choice) {
            return choice +1;
        }

        @Override
        public Integer lastChoice(Square point) {
            return n;
        }

        @Override
        public boolean assignable(Integer choice, Square point) {
            int tmp = point.getValue(); // memorizzo la scelta corrente
            point.setValue(choice); // assegno temporaneamente la nuova scelta per vedere se rispetta i vincoli
            List<Square> duplicateSquares = findDuplicates();
            if(duplicateSquares.isEmpty()) {
                // se non ci sono duplicati individuo il blocco di appartenenza della cella corrente e verifico il vincolo aritmetico
                for(Cage c : cageSchema) {
                    for(Square s : c.getSquares()) {
                        if(s.equals(point)) {
                            if(!c.checkTargetResult()) {
                                point.setValue(tmp); // ripristino la vecchia griglia
                                return false;
                            }
                            return true;
                        }
                    }
                }
            }
            point.setValue(tmp);
            return false;
        }

        @Override
        public void assign(Integer choice, Square point) {
            point.setValue(choice);
        }

        @Override
        public void deassign(Integer choice, Square point) {
            point.setValue(0);
        }

        @Override
        public Square previousDecisionPoint(Square point) {
            int row = point.getRow();
            int column = point.getColumn();
            if(column > 0) column--;
            else if(column == 0) {
                row--;
                column = n-1;
            }
            return squares[row][column];
        }

        @Override
        public Integer lastChoiceAssignedTo(Square point) {
            return point.getValue();
        }

        @Override
        public void writeSolution(int nSol) {
            int[][] gridCopy = new int[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    gridCopy[i][j] = squares[i][j].getValue();
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
                for (Square square : cage.getSquares()) {
                    Square s = new Square(square);
                    squares[s.getRow()][s.getColumn()] = s;
                    squareList.add(s);
                }
                cageSchema.add(new Cage(squareList.toArray(new Square[0]), cage.getResult(), cage.getOperation()));
            }
        }
    }

    @Override public String toString() {
        int[][] ret = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                ret[i][j] = squares[i][j].getValue();
            }
        }
        return Arrays.deepToString(ret);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(! (o instanceof AbstractGrid)) return false;
        Grid grid = (Grid) o;
        for(Cage c1 : this.cageSchema) {
            boolean cageFound = false;
            for(Cage c2 : grid.cageSchema) {
                if(c1.equals(c2)) cageFound = true;
            }
            if(!cageFound) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int M = 17;
        int h = 1;
        for(Cage c : cageSchema) {
            for(Square s : c.getSquares()) {
                h = h * M + s.hashCode();
            }
        }
        return h;
    }

}

package mvc.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Cage implements Serializable {

    private static final long serialVersionUID = -328720550746986579L;

    // ogni blocco dello schema di gioco (Cage) tiene dei riferimenti alle celle della griglia di gioco che lo costituiscono
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

    private void swap(Square[] a, int i, int j) {
        Square tmp = a[i];
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

    // Effettua ricorsivamente delle permutazioni sull'array di celle per verificare il soddisfacimento del vincolo
    // aritmetico, che dipende dall'ordine degli operandi
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

    public static boolean verifyAdjacency(boolean[][] squares) {
        int n = squares.length;
        boolean[][] selectionSnapshot = new boolean[n][n];
        int selectionSize = 0;
        int startRow = 0;
        int startColumn = 0;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (squares[i][j]) {
                    selectionSize++;
                    selectionSnapshot[i][j] = true;
                    startRow = i;
                    startColumn = j;
                }

        return verifyAdjacency(startRow,startColumn,selectionSize-1, selectionSnapshot) == 0;
    }

    private static int verifyAdjacency(int i, int j, int remaining, boolean[][] squares) {
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

    @Override
    public String toString() {
        return Arrays.deepToString(squares);
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Cage)) return false;
        Cage c = (Cage) o;
        for(Square s1 : this.squares) {
            boolean squareFound = false;
            for(Square s2 : c.squares) {
                if(s1.equals(s2)) squareFound = true;
            }
            if(!squareFound) return false;
        }
        return this.result == c.result && this.operation == c.operation;
    }

    @Override
    public int hashCode() {
        final int M = 17;
        int h = 1;
        for(Square s: squares) {
            h = h * M + s.hashCode();
        }
        h = h * M + result;
        h = h * M + operation.hashCode();
        return h;
    }

}

package mvc.model;

public class GridEvent {

    private final Grid source;
    private boolean  newGrid = false;
    private boolean squareSelected = false;
    private boolean cageCreated = false;
    private Square selectedSquare;
    private boolean[][] selectionSnapshot;
    private MathOperation operation;
    private int result;

    public GridEvent(Grid source) {
        this.source = source;
    }

    public GridEvent(Grid source, boolean newGrid) {
        this.source = source;
        this.newGrid = newGrid;
    }

    public GridEvent(Grid source, boolean squareSelected, Square selectedSquare) {
        this.source = source;
        this.squareSelected = squareSelected;
        this.selectedSquare = selectedSquare;
    }

    public GridEvent(Grid source, boolean cageCreated, boolean[][] selectionSnapshot, MathOperation operation, int result) {
        this.source = source;
        this.cageCreated = cageCreated;
        this.selectionSnapshot = selectionSnapshot;
        this.operation = operation;
        this.result = result;
    }

    public Grid getSource() {return source;}

    public boolean isSquareSelected() {
        return squareSelected;
    }

    public boolean isCageCreated() {
        return cageCreated;
    }

    public boolean isNewGrid() {
        return newGrid;
    }

    public Square getSelectedSquare() {
        return selectedSquare;
    }

    public boolean[][] getSelectionSnapshot() {
        return selectionSnapshot;
    }

    public MathOperation getOperation() {
        return operation;
    }

    public int getResult() {
        return result;
    }
}

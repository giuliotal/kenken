package mvc.model;

import java.util.List;

public class GridEvent {

    private final Grid source;
    private final boolean newGrid;
    private final boolean squareSelected;
    private final boolean cageCreated;
    private final boolean cageCleared;
    private final boolean constraintChecked;
    private final boolean numberInserted;
    private final boolean solutionRequested;

    private final Square selectedSquare;
    private final boolean[][] selectionSnapshot;
    private final MathOperation operation;
    private final int result;

    private final List<Square> duplicateSquares;
    private final List<Square> invalidTargetResultSquares;

    public boolean isSquareSelected() {
        return squareSelected;
    }

    public boolean isCageCreated() {
        return cageCreated;
    }

    public boolean isNewGrid() {
        return newGrid;
    }

    public boolean isCageCleared() { return cageCleared; }

    public boolean isConstraintChecked() { return constraintChecked; }

    public boolean isNumberInserted() { return numberInserted; }

    public boolean isSolutionRequested() { return solutionRequested; }



    public Grid getSource() {return source;}

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

    public List<Square> getDuplicateSquares() { return duplicateSquares; }

    public List<Square> getInvalidTargetResultSquares() { return invalidTargetResultSquares; }

    public static class Builder {
        private final Grid source;

        private boolean newGrid = false;
        private boolean squareSelected = false;
        private boolean cageCreated = false;
        private boolean cageCleared = false;
        private boolean constraintChecked = false;
        private boolean numberInserted = false;
        private boolean solutionRequested = false;

        private Square selectedSquare;
        private boolean[][] selectionSnapshot;
        private MathOperation operation;
        private int result;

        private List<Square> duplicateSquares;
        private List<Square> invalidTargetResultSquares;

        public Builder(Grid grid) {this.source = grid;}

        public Builder newGrid(boolean b) { newGrid = b; return this;}
        public Builder squareSelected(boolean b) { squareSelected = b; return this;}
        public Builder cageCreated(boolean b) { cageCreated = b; return this;}
        public Builder cageCleared(boolean b) { cageCleared = b; return this;}
        public Builder constraintsChecked(boolean b) { constraintChecked = b; return this;}
        public Builder numberInserted(boolean b) { numberInserted = b; return this;}
        public Builder solutionRequested(boolean b) { solutionRequested = b; return this;}

        public Builder selectedSquare(Square selectedSquare) { this.selectedSquare = selectedSquare; return this; }
        public Builder selectionSnapshot(boolean[][] selectionSnapshot) { this.selectionSnapshot = selectionSnapshot; return this; }
        public Builder operation(MathOperation operation) { this.operation = operation; return this; }
        public Builder result(int result) { this.result = result; return this; }

        public Builder duplicateSquares(List<Square> duplicateSquares) { this.duplicateSquares = duplicateSquares; return this;}
        public Builder invalidTargetResultSquares(List<Square> invalidTargetResultSquares) { this.invalidTargetResultSquares = invalidTargetResultSquares; return this;}

        public GridEvent build() { return new GridEvent(this); }

    }

    private GridEvent(Builder builder) {
        source = builder.source;

        newGrid = builder.newGrid; squareSelected = builder.squareSelected;
        cageCreated = builder.cageCreated; cageCleared = builder.cageCleared;
        constraintChecked = builder.constraintChecked; numberInserted = builder.numberInserted;
        solutionRequested = builder.solutionRequested; duplicateSquares = builder.duplicateSquares;
        invalidTargetResultSquares = builder.invalidTargetResultSquares;
        selectedSquare = builder.selectedSquare; selectionSnapshot = builder.selectionSnapshot;
        operation = builder.operation; result = builder.result;
    }

}

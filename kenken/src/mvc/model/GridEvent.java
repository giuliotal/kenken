package mvc.model;

public class GridEvent {

    private final Grid source;
    private final boolean newGrid;
    private final boolean cageCreated;
    private final boolean cageCleared;
    private final boolean constraintChecked;
    private final boolean numberInserted;
    private final boolean numberDeleted;
    private final boolean solutionRequested;

    public boolean isCageCreated() {
        return cageCreated;
    }
    public boolean isNewGrid() {
        return newGrid;
    }
    public boolean isCageCleared() { return cageCleared; }
    public boolean isConstraintChecked() { return constraintChecked; }
    public boolean isNumberInserted() { return numberInserted; }
    public boolean isNumberDeleted() { return numberDeleted; }
    public boolean isSolutionRequested() { return solutionRequested; }


    public Grid getSource() {return source;}

    public static class Builder {
        private final Grid source;

        private boolean newGrid = false;
        private boolean cageCreated = false;
        private boolean cageCleared = false;
        private boolean constraintChecked = false;
        private boolean numberInserted = false;
        private boolean numberDeleted = false;
        private boolean solutionRequested = false;

        public Builder(Grid grid) {this.source = grid;}

        public Builder newGrid(boolean b) { newGrid = b; return this;}
        public Builder cageCreated(boolean b) { cageCreated = b; return this;}
        public Builder cageCleared(boolean b) { cageCleared = b; return this;}
        public Builder constraintsChecked(boolean b) { constraintChecked = b; return this;}
        public Builder numberInserted(boolean b) { numberInserted = b; return this;}
        public Builder numberDeleted(boolean b) { numberDeleted = b; return this;}
        public Builder solutionRequested(boolean b) { solutionRequested = b; return this;}

        public GridEvent build() { return new GridEvent(this); }

    }

    private GridEvent(Builder builder) {
        source = builder.source;

        newGrid = builder.newGrid; cageCreated = builder.cageCreated; cageCleared = builder.cageCleared;
        constraintChecked = builder.constraintChecked; numberInserted = builder.numberInserted;
        numberDeleted = builder.numberDeleted; solutionRequested = builder.solutionRequested;
    }

}

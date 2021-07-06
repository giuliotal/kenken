package mvc.gridCommand;

import command.Command;
import mvc.model.Grid;
import mvc.model.GridInterface;
import mvc.model.Memento;

public class CreateCageCommand implements Command {

    private final GridInterface grid;
    private final boolean[][] selectedSquares;
    private final int result;
    private final Grid.MathOperation operation;
    private final Memento gridMemento;

    public CreateCageCommand(GridInterface grid, boolean[][] selectedSquares, int result, Grid.MathOperation operation) {
        this.grid = grid;
        this.selectedSquares = selectedSquares;
        this.result = result;
        this.operation = operation;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        grid.createCage(selectedSquares, result, operation);
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return true;
    }

}

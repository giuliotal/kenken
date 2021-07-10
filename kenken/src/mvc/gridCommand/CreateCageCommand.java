package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.MathOperation;
import mvc.model.Memento;
import mvc.model.Square;

public class CreateCageCommand implements Command {

    private final GridInterface grid;
    private final Square[] selectedSquares;
    private final int result;
    private final MathOperation operation;
    private final Memento gridMemento;

    public CreateCageCommand(GridInterface grid, Square[] selectedSquares, int result, MathOperation operation) {
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

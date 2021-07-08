package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.Memento;

public class InsertNumberCommand implements Command {

    private final GridInterface grid;
    private final int row;
    private final int column;
    private final int number;
    private final Memento gridMemento;

    public InsertNumberCommand(GridInterface grid, int row, int column, int number) {
        this.grid = grid;
        this.row = row;
        this.column = column;
        this.number = number;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        grid.insertNumber(number, row, column);
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return false;
    }
}

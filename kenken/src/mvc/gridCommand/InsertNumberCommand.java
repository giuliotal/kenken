package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;

public class InsertNumberCommand implements Command {

    private final GridInterface grid;
    private final int row;
    private final int column;
    private final int number;

    public InsertNumberCommand(GridInterface grid, int row, int column, int number) {
        this.grid = grid;
        this.row = row;
        this.column = column;
        this.number = number;
    }

    @Override
    public boolean doIt() {
        grid.insertNumber(number, row, column);
        return false;
    }

    @Override
    public boolean undoIt() {
        grid.deleteNumber(row,column);
        return false;
    }
}

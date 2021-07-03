package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;

public class ClearGridCommand implements Command {

    private final GridInterface grid;

    public ClearGridCommand(GridInterface grid) {
        this.grid = grid;
    }

    @Override
    public boolean doIt() {
        grid.clear();
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

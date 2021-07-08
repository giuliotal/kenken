package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;

public class CheckConstraintsCommand implements Command {

    private final GridInterface grid;

    public CheckConstraintsCommand(GridInterface grid) {
        this.grid = grid;
    }

    @Override
    public boolean doIt() {
        grid.checkConstraints();
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

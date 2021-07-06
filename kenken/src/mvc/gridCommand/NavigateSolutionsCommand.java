package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;

public class NavigateSolutionsCommand implements Command {

    private final GridInterface grid;
    private final int i;

    public NavigateSolutionsCommand(GridInterface grid, int i) {
        this.grid = grid;
        this.i = i;
    }

    @Override
    public boolean doIt() {
        if(i > 0)
            grid.nextSolution();
        else if(i < 0)
            grid.previousSolution();
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

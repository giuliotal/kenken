package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.Memento;

public class ShowSolutionsCommand implements Command {

    private final GridInterface grid;
    private final int maxSolutions;
    private final Memento gridMemento;

    public ShowSolutionsCommand(GridInterface grid, int maxSolutions) {
        this.grid = grid;
        this.maxSolutions = maxSolutions;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        if(maxSolutions == -1) return false;
        grid.findSolutions(maxSolutions);
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return true;
    }
}

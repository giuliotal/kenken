package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.Memento;
import mvc.model.exceptions.SolutionsNotFoundException;
import mvc.view.GridPanel;

public class ShowSolutionsCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final int maxSolutions;
    private final Memento gridMemento;

    public ShowSolutionsCommand(GridInterface grid, GridPanel gridPanel, int maxSolutions) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.maxSolutions = maxSolutions;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        if(maxSolutions == -1) return false;
        try {
            grid.findSolutions(maxSolutions);
        } catch (SolutionsNotFoundException e) {
            gridPanel.showSolutionsNotFoundDialog();
        }
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return true;
    }
}

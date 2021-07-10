package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.Memento;
import mvc.model.exceptions.SolutionsNotFoundException;
import mvc.view.GridPanel;

public class ShowSolutionsCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final Memento gridMemento;

    public ShowSolutionsCommand(GridInterface grid, GridPanel gridPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        int maxSolutions = gridPanel.getMaxSolutionsInput();
        if(maxSolutions == -1) return true;
        try {
            grid.findSolutions(maxSolutions);
        } catch (SolutionsNotFoundException e) {
            gridPanel.showErrorDialog("Unsolvable game!",
                    "There are no solutions for this game!");
        }
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return true;
    }
}

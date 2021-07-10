package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

public class CreateGridCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;
    private final int n;

    public CreateGridCommand(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel, int n) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
        this.n = n;
    }


    @Override
    public boolean doIt() {
        if(grid.getSize()!= 0 && !gridPanel.showNewGameDialog()) return false;
        grid.setSize(n);
        controllerPanel.setCreateCageButton(true);
        controllerPanel.setClearGridButton(false);
        controllerPanel.setCheckConstraintsButton(false);
        controllerPanel.setShowSolutionsButton(false);
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

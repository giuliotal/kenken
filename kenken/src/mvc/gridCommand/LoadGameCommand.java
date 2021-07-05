package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

public class LoadGameCommand implements Command {


    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;

    public LoadGameCommand(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
    }

    @Override
    public boolean doIt() {
        String filePath = gridPanel.getFilePath();
        if(filePath != null) {
            if(!grid.load(filePath)) gridPanel.showIOErrorDialog();
            controllerPanel.setStartGameButton(false);
            controllerPanel.setCreateCageButton(false);
            controllerPanel.setCheckConstraintsButton(true);
            controllerPanel.setClearGridButton(true);
            controllerPanel.setShowSolutionsButton(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

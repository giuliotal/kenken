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
        String filePath = gridPanel.getFilePathInput(GridPanel.FILE_OP.LOAD_OP);
        if(filePath != null) {
            if(!grid.load(filePath))
                gridPanel.showErrorDialog("An error occurred trying to load the game",
                        "The file selected may be damaged or incompatible");
            else {
                controllerPanel.setStartGameButton(true);
                controllerPanel.setCreateCageButton(false);
                controllerPanel.setCheckConstraintsButton(false);
                controllerPanel.setClearGridButton(false);
                controllerPanel.setShowSolutionsButton(false);
            }
        }
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.view.GridPanel;

public class StartGameCommand implements Command {

    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;

    public StartGameCommand(GridPanel gridPanel, ControllerPanel controllerPanel) {
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
    }

    @Override
    public boolean doIt() {
        gridPanel.startGameView();
        controllerPanel.setStartGameButtonEnabled(false);
        controllerPanel.setCheckConstraintsButton(true);
        controllerPanel.setClearGridButton(true);
        controllerPanel.setShowSolutionsButton(true);
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

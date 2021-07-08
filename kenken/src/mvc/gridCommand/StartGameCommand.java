package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.model.GridInterface;
import mvc.model.Memento;
import mvc.view.GridPanel;

public class StartGameCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;
    private final Memento gridMemento;

    public StartGameCommand(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        gridPanel.startGameView();
        controllerPanel.setStartGameButton(false);
        controllerPanel.setCheckConstraintsButton(true);
        controllerPanel.setClearGridButton(true);
        controllerPanel.setShowSolutionsButton(true);
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

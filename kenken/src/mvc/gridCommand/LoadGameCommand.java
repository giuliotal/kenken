package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.model.GridInterface;
import mvc.model.Memento;
import mvc.view.GridPanel;

public class LoadGameCommand implements Command {


    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;
    private final Memento gridMemento;

    public LoadGameCommand(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        String filePath = gridPanel.getFilePath();
        if(filePath != null) {
            if(!grid.load(filePath)) gridPanel.showIOErrorDialog();
            controllerPanel.enableControlButtons();
            controllerPanel.setStartGameButton(true);
            controllerPanel.setCreateCageButton(false);
            controllerPanel.setCheckConstraintsButton(false);
            controllerPanel.setClearGridButton(false);
            controllerPanel.setShowSolutionsButton(false);
            return true;
        }
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return true;
    }
}

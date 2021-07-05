package mvc.view;

import command.HistoryCommandHandler;
import mvc.controller.ControllerPanel;
import mvc.gridCommand.LoadGameCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoadGameAction extends AbstractAction {


    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;
    private final HistoryCommandHandler commandHandler;

    public LoadGameAction(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel, HistoryCommandHandler commandHandler) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new LoadGameCommand(grid, gridPanel, controllerPanel));
    }
}

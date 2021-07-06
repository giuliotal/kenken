package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.StartGameCommand;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StartGameAction extends AbstractAction {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;
    private final CommandHandler commandHandler;

    public StartGameAction(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel, CommandHandler commandHandler) {
        this.grid = grid;
        this.commandHandler = commandHandler;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new StartGameCommand(grid, gridPanel, controllerPanel));
    }
}

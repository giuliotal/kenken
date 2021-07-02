package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.StartGameCommand;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StartGameAction extends AbstractAction {

    private final CommandHandler commandHandler;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;

    public StartGameAction(CommandHandler commandHandler, GridPanel gridPanel, ControllerPanel controllerPanel) {
        this.commandHandler = commandHandler;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new StartGameCommand(gridPanel, controllerPanel));
    }
}

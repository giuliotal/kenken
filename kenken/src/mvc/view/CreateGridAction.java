package mvc.view;

import command.CommandHandler;
import mvc.controller.ControllerPanel;
import mvc.gridCommand.CreateGridCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateGridAction extends AbstractAction {

    GridInterface grid;
    GridPanel gridPanel;
    ControllerPanel controllerPanel;
    CommandHandler commandHandler;
    int n;

    public CreateGridAction(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel, CommandHandler commandHandler, int n) {
        super(n+"x"+n);
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
        this.commandHandler = commandHandler;
        this.n = n;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new CreateGridCommand(grid, gridPanel, controllerPanel, n));
    }
}

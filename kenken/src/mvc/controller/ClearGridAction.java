package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.ClearGridCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ClearGridAction extends AbstractAction {

    private final GridInterface grid;
    private final CommandHandler commandHandler;

    public ClearGridAction(GridInterface grid, CommandHandler commandHandler) {
        this.grid = grid;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new ClearGridCommand(grid));
    }
}

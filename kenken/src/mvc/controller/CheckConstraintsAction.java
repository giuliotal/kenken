package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.CheckConstraintsCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CheckConstraintsAction extends AbstractAction {

    private final GridInterface grid;
    private final CommandHandler commandHandler;

    public CheckConstraintsAction(GridInterface grid, CommandHandler commandHandler) {
        this.grid = grid;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new CheckConstraintsCommand(grid));
    }
}

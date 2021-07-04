package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.NavigateSolutionsCommand;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NavigateSolutionsAction extends AbstractAction {


    private final GridInterface grid;
    private final CommandHandler commandHandler;
    private final int i;

    public NavigateSolutionsAction(GridInterface grid, CommandHandler commandHandler, int i) {
        this.grid = grid;
        this.commandHandler = commandHandler;
        this.i = i;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new NavigateSolutionsCommand(grid, i));
    }
}

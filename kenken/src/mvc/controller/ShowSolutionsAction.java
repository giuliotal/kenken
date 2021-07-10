package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.ShowSolutionsCommand;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowSolutionsAction extends AbstractAction {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final CommandHandler commandHandler;

    public ShowSolutionsAction(GridInterface grid, GridPanel gridPanel, CommandHandler commandHandler) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int maxSolutions = gridPanel.getMaxSolutions();
        commandHandler.handle(new ShowSolutionsCommand(grid, gridPanel, maxSolutions));
    }
}

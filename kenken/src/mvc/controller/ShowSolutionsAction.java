package mvc.controller;

import command.CommandHandler;
import mvc.gridCommand.ShowSolutionsCommand;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowSolutionsAction extends AbstractAction {

    private final GridInterface grid;
    private final ControllerPanel controllerPanel;
    private final CommandHandler commandHandler;

    public ShowSolutionsAction(GridInterface grid, ControllerPanel controllerPanel, CommandHandler commandHandler) {
        this.grid = grid;
        this.controllerPanel = controllerPanel;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int maxSolutions = controllerPanel.getMaxSolutions();
        commandHandler.handle(new ShowSolutionsCommand(grid, maxSolutions));
    }
}

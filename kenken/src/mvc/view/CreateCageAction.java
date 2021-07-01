package mvc.view;

import command.CommandHandler;
import mvc.gridCommand.CreateCageCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CreateCageAction extends AbstractAction {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final CommandHandler commandHandler;

    public CreateCageAction(GridInterface grid, CommandHandler commandHandler, GridPanel gridPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new CreateCageCommand(grid, gridPanel));
    }
}

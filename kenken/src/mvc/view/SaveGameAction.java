package mvc.view;

import command.HistoryCommandHandler;
import mvc.gridCommand.SaveGameCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveGameAction extends AbstractAction {


    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final HistoryCommandHandler commandHandler;

    public SaveGameAction(GridInterface grid, GridPanel gridPanel, HistoryCommandHandler commandHandler) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new SaveGameCommand(grid, gridPanel));
    }
}

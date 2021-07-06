package mvc.view;

import command.CommandHandler;
import mvc.gridCommand.CreateCageCommand;
import mvc.model.Grid;
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
        if(!gridPanel.checkSelection()) return;

        boolean[][] selectedSquares = gridPanel.getSelectedSquares();

        int result = gridPanel.getTargetResult();
        if(result == -1) return;

        Grid.MathOperation operation = gridPanel.getOperation();
        if(operation == null) return;
        commandHandler.handle(new CreateCageCommand(grid, selectedSquares, result, operation));
    }
}

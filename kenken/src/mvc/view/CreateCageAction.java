package mvc.view;

import command.CommandHandler;
import mvc.gridCommand.CreateCageCommand;
import mvc.model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

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

        boolean[][] selectedSquares = gridPanel.getSelectedSquares();
        boolean buttonSelected = false;
        for (int i = 0; i < selectedSquares.length && !buttonSelected; i++)
            for (int j = 0; j < selectedSquares.length && !buttonSelected; j++)
                if(selectedSquares[i][j]) buttonSelected = true;

        if(!buttonSelected) {
            gridPanel.showSelectionError();
            return;
        }
        if(!Cage.verifyAdjacency(selectedSquares)) {
            gridPanel.showAdjacencyError();
            return;
        }

        int result = gridPanel.getTargetResult();
        if(result == -1) return;

        MathOperation operation = gridPanel.getOperation();
        if(operation == null) return;

        List<Square> squares = new LinkedList<>();
        for (int i = 0; i < selectedSquares.length; i++) {
            for (int j = 0; j < selectedSquares.length; j++) {
                if(selectedSquares[i][j]) squares.add(new Square(i,j));
            }
        }

        commandHandler.handle(new CreateCageCommand(grid, squares.toArray(new Square[squares.size()]), result, operation));
    }
}

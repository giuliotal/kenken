package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SelectSquareCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ActionEvent actionEvent;

    public SelectSquareCommand(GridInterface grid, GridPanel gridPanel, ActionEvent actionEvent) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.actionEvent = actionEvent;
    }

    @Override
    public boolean doIt() {
        int n = grid.getSize();
        JButton[][] buttonGrid = gridPanel.getButtonGrid();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(actionEvent.getSource() == buttonGrid[i][j]) {
                    grid.selectSquare(i,j);
                }
            }
        }
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

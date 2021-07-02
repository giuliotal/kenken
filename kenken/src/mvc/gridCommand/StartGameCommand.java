package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;

public class StartGameCommand implements Command {

    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;

    public StartGameCommand(GridPanel gridPanel, ControllerPanel controllerPanel) {
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
    }

    @Override
    public boolean doIt() {
        JButton[][] buttonGrid = gridPanel.getButtonGrid();
        int n = buttonGrid.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                JTextField input = new JTextField();
                input.setFont(input.getFont().deriveFont(Font.PLAIN, 50));
                buttonGrid[i][j].setLayout(new GridLayout(2,3));
                buttonGrid[i][j].add(Box.createVerticalGlue());
                buttonGrid[i][j].add(Box.createVerticalGlue());
                buttonGrid[i][j].add(Box.createVerticalGlue());
                buttonGrid[i][j].add(Box.createVerticalGlue());
                buttonGrid[i][j].add(input);
                buttonGrid[i][j].add(Box.createVerticalGlue());
            }
        }
        gridPanel.revalidate();
        controllerPanel.getStartGameButton().setEnabled(false);
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

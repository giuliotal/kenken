package mvc.view;

import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SelectCellAction extends AbstractAction {

    GridInterface grid;
    GridPanel panel;

    public SelectCellAction(GridInterface grid, GridPanel panel){
        this.grid = grid;
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        int n = grid.getSize();
        JButton[][] buttonGrid = panel.getButtonGrid();
        boolean[][] buttonSelection = panel.getSelectedSquares();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(actionEvent.getSource() == buttonGrid[i][j]) {
                    buttonSelection[i][j] = !buttonSelection[i][j];
                    buttonGrid[i][j].setBackground(panel.getSelectedSquares()[i][j] ? Color.BLUE : Color.WHITE);
                }
            }

        }
    }
}

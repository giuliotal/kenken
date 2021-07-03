package mvc.gridCommand;

import command.Command;
import mvc.controller.ControllerPanel;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;

public class CreateGridCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;
    private final ControllerPanel controllerPanel;
    private final int n;

    public CreateGridCommand(GridInterface grid, GridPanel gridPanel, ControllerPanel controllerPanel, int n) {
        this.grid = grid;
        this.gridPanel = gridPanel;
        this.controllerPanel = controllerPanel;
        this.n = n;
    }


    @Override
    public boolean doIt() {
        if(grid.getSize()!=0){
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(gridPanel);
            int ret = JOptionPane.showConfirmDialog(topFrame,
                    "Are you sure you want to create a new game?\n" +
                            "All unsaved progress will be lost.", "Are you sure?", JOptionPane.YES_NO_OPTION);
            if(ret != JOptionPane.YES_OPTION) return false;
        }
        grid.setSize(n);
        controllerPanel.enableControlButtons();
        return true;
    }

    @Override
    public boolean undoIt() {
        return true;
    }
}

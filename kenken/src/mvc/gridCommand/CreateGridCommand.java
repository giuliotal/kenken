package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;

public class CreateGridCommand implements Command {

    private final GridInterface grid;
    private final GridPanel panel;
    private final int n;

    public CreateGridCommand(GridInterface grid, GridPanel panel, int n) {
        this.grid = grid;
        this.panel = panel;
        this.n = n;
    }


    @Override
    public boolean doIt() {
        if(grid.getSize()!=0){
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
            int ret = JOptionPane.showConfirmDialog(topFrame,
                    "Are you sure you want to create a new game?\n" +
                            "All unsaved progress will be lost.", "Are you sure?", JOptionPane.YES_NO_OPTION);
            if(ret != JOptionPane.YES_OPTION) return false;
        }
        grid.setSize(n);
        return true;
    }

    @Override
    public boolean undoIt() {
        return true;
    }
}

package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;

public class ShowSolutionsCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;

    public ShowSolutionsCommand(GridInterface grid, GridPanel gridPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
    }

    @Override
    public boolean doIt() {
        //TODO richiesta del numero di soluzioni da spostare nella view
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(gridPanel);
        int maxSolutions = 0;
        boolean validInput = false;
        do {
            String input = JOptionPane.showInputDialog(topFrame, "Enter a maximum value of solutions to display:");
            if (input == null)
                return false;
            try {
                maxSolutions = Integer.parseInt(input);
                validInput = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(topFrame, "Please insert an integer value.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
            grid.findSolutions(maxSolutions);
        }while(!validInput);
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

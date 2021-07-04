package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.MathOperation;
import mvc.view.GridPanel;

import javax.swing.*;

public class CreateCageCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;

    public CreateCageCommand(GridInterface grid, GridPanel gridPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
    }

    @Override
    public boolean doIt() {
        //TODO tutti i valori di input devono essere richiesti con appositi metodi nella view e non nel controller
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(gridPanel);

        if(grid.isSelectionEmpty()) {
            JOptionPane.showMessageDialog(topFrame, "At least one square must be selected",
                    "Invalid cage selection", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(!grid.verifyAdjacency()) {
            JOptionPane.showMessageDialog(topFrame,"Square selected have to be adjacent in order to bulid a cage.\n" +
                    "Please change selection.", "Invalid cage selection",JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean targetResultObtained = false;
        int result = 0;
        do {
            String input = JOptionPane.showInputDialog(topFrame,"Enter the target result:");
            if(input==null)
                return false;
            try {
                result = Integer.parseInt(input);
                targetResultObtained = true;
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(topFrame,"Please insert an integer value.", "Invalid target result",JOptionPane.ERROR_MESSAGE);
            }
        }while(!targetResultObtained);

        boolean operationObtained = false;
        String operation = null;
        do {
            String input = JOptionPane.showInputDialog(topFrame,"Enter the math operation:");
            if(input==null)
                return false;
            if(input.matches("[\\+\\-\\*/]")) {
                operation = input;
                operationObtained = true;
            }
            else {
                JOptionPane.showMessageDialog(topFrame,"Please insert one of the allowed operator symbols: +, -, *, /.",
                        "Invalid operator symbol",JOptionPane.ERROR_MESSAGE);
            }
        }while(!operationObtained);

        MathOperation mathOperation = null;
        if(operation != null){
            switch(operation){
                case "+": mathOperation = MathOperation.SUM; break;
                case "*": mathOperation = MathOperation.MULTIPLICATION; break;
                case "-": mathOperation = MathOperation.SUBTRACTION; break;
                case "/": mathOperation = MathOperation.DIVISION; break;
            }
        }

        grid.createCage(result, mathOperation);

        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }

}

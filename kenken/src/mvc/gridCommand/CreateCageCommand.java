package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.MathOperation;
import mvc.model.Square;
import mvc.view.GridPanel;

import javax.swing.*;
import java.util.LinkedList;

public class CreateCageCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;

    public CreateCageCommand(GridInterface grid, GridPanel gridPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
    }

    @Override
    public boolean doIt() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(gridPanel);
        int n = grid.getSize();
        boolean[][] selectedSquares = gridPanel.getSelectedSquares();
        LinkedList<Square> cage = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(selectedSquares[i][j]) {
                    Square s = new Square(i, j);
                    if(cage.isEmpty())
                        cage.add(s);
                    else if(s.isAdjacentTo(cage.getLast()))
                        cage.add(s);
                    else {
                        JOptionPane.showMessageDialog(topFrame,"Square selected have to be adjacent in order to bulid a cage.\n" +
                                "Please change selection.", "Invalid cage selection",JOptionPane.ERROR_MESSAGE);
                        return false;
                    }
                }
            }
        }
        if(cage.isEmpty()) {
            JOptionPane.showMessageDialog(topFrame, "At least one square must be selected",
                    "Invalid cage selection", JOptionPane.ERROR_MESSAGE);
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
                if(result==JOptionPane.CANCEL_OPTION)
                    return false;
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
        grid.createCage(cage.toArray(new Square[cage.size()]), result, mathOperation);
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }

}

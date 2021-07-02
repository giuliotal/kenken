package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.MathOperation;
import mvc.model.Square;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;
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
                    cage.add(new Square(i, j));
                }
            }
        }
        if(cage.isEmpty()) {
            JOptionPane.showMessageDialog(topFrame, "At least one square must be selected",
                    "Invalid cage selection", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if(!verifyAdjacency(selectedSquares)) {
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

        // rende persistente la selezione di celle appena confermata dall'utente
        JButton[][] buttonGrid = gridPanel.getButtonGrid();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(selectedSquares[i][j]){
                    JButton button = buttonGrid[i][j];
                    button.setBackground(Color.WHITE);
                    button.setEnabled(false);
                    int top, left, bottom, right;
                    top = left = bottom = right = 4;
                    if(i > 0 && selectedSquares[i-1][j]) top = 1;
                    if(i < selectedSquares.length-1 && selectedSquares[i+1][j]) bottom = 1;
                    if(j > 0 && selectedSquares[i][j-1]) left = 1;
                    if(j < selectedSquares.length-1 && selectedSquares[i][j+1]) right = 1;
                    button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
                }
            }
        }
        gridPanel.resetSelection();
        return true;
    }

    @Override
    public boolean undoIt() {
        return false;
    }

    private boolean verifyAdjacency(boolean[][] squares) {
        int n = squares.length;
        boolean[][] selection = new boolean[n][n];
        int selectionSize = 0;
        int startRow = 0;
        int startColumn = 0;

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (squares[i][j]) {
                    selectionSize++;
                    selection[i][j] = true;
                    startRow = i;
                    startColumn = j;
                }

        int i = startRow;
        int j = startColumn;

        return verifyAdjacency(i,j,selectionSize-1, selection) == 0;
    }

    private int verifyAdjacency(int i, int j, int remaining, boolean[][] squares) {
        if(i > 0 && squares[i-1][j]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i-1,j,remaining-1,squares); // up
        }
        if(i < squares.length-1 && squares[i+1][j]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i+1,j,remaining-1,squares); // down
        }
        if(j > 0 && squares[i][j-1]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i,j-1,remaining-1,squares); // left
        }
        if(j < squares.length-1 && squares[i][j+1]) {
            squares[i][j] = false;
            remaining = verifyAdjacency(i,j+1,remaining-1,squares); //right
        }
        squares[i][j] = false;
        return remaining;
    }

}

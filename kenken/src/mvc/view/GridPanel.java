package mvc.view;

import command.CommandHandler;
import mvc.model.GridEvent;
import mvc.model.GridInterface;
import mvc.model.GridListener;
import mvc.model.Square;

import javax.swing.*;
import java.awt.*;

// Concrete Observer (VIEW)
public class GridPanel extends JPanel implements GridListener {

    // subject
    private final GridInterface grid;
    // riferimento al controller per inoltrare la gestione degli eventi ricevuti dalla view (es. click sui bottoni)
    private final CommandHandler commandHandler;
    private int gridSize;
    private JButton[][] buttonGrid;

    public GridPanel(GridInterface grid, CommandHandler commandHandler) {
        this.grid = grid;
        this.gridSize = grid.getSize();
        this.buttonGrid = new JButton[gridSize][gridSize];
        this.commandHandler = commandHandler;
        grid.addGridListener(this);
    }

    public JButton[][] getButtonGrid() {
        return buttonGrid;
    } //TODO dovrei restituire una copia?

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void gridChanged(GridEvent e) {
        int n = e.getSource().getSize();
        if(e.isNewGrid()) { // se le dimensioni della griglia sono cambiate, ricostruisco interamente il pannello
            this.gridSize = n;
            this.buttonGrid = new JButton[n][n];
            rebuildGrid();
        }
        if(e.isSquareSelected()) {
            Square selectedSquare = e.getSelectedSquare();
            int i = selectedSquare.getRow();
            int j = selectedSquare.getColumn();
            buttonGrid[i][j].setBackground(e.getSource().getSelectedSquares()[i][j] ? Color.BLUE : Color.WHITE);
        }
        if(e.isCageCreated()) {
            // rende persistente la selezione di celle appena confermata dall'utente
            boolean[][] selectedSquares = e.getSelectionSnapshot();
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
        }
        repaint();
        revalidate();
    }

    private void rebuildGrid() {
        this.removeAll();
        this.setLayout(new GridLayout(gridSize, gridSize));
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton square = new JButton(new SelectSquareAction(grid, this, commandHandler));
                buttonGrid[i][j] = square;
                square.setPreferredSize(new Dimension(80, 80));
                square.setBackground(Color.WHITE);
                square.setOpaque(true);
                // comunico la pressione del bottone al controller, che la gestisce opportunamente con un apposito command
                // N.B. la view raccoglie l'interazione dell'utente ma è il controller che la processa
                //gridButton.addActionListener();
                add(square);
            }
        }
    }


}

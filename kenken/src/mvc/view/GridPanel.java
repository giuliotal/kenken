package mvc.view;

import command.CommandHandler;
import mvc.model.GridEvent;
import mvc.model.GridInterface;
import mvc.model.GridListener;

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
    private boolean[][] selectedSquares;

    public GridPanel(GridInterface grid, CommandHandler commandHandler) {
        this.grid = grid;
        this.gridSize = grid.getSize();
        this.buttonGrid = new JButton[gridSize][gridSize];
        this.selectedSquares = new boolean[gridSize][gridSize];
        this.commandHandler = commandHandler;
        grid.addGridListener(this);
    }

    public JButton[][] getButtonGrid() {
        return buttonGrid;
    } //TODO dovrei restituire una copia?

    public boolean[][] getSelectedSquares() {
        return selectedSquares;
    }

    public void resetSelection() {
        selectedSquares = new boolean[gridSize][gridSize];
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void gridChanged(GridEvent e) {
        int n = e.getSource().getSize();
        if(n != gridSize) { // se le dimensioni della griglia sono cambiate, ricostruisco interamente il pannello
            this.gridSize = n;
            this.buttonGrid = new JButton[n][n];
            this.selectedSquares = new boolean[n][n];
            rebuildGrid();
        }
        repaint();
        revalidate();
    }

    private void rebuildGrid() {
        this.removeAll();
        this.setLayout(new GridLayout(gridSize, gridSize));
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton square = new JButton(new SelectCellAction(grid, this));
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

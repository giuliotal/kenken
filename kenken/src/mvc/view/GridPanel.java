package mvc.view;

import command.CommandHandler;
import mvc.gridCommand.InsertNumberCommand;
import mvc.model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

// Concrete Observer (VIEW)
public class GridPanel extends JPanel implements GridListener {

    // subject
    private final GridInterface grid;
    // riferimento al controller per inoltrare la gestione degli eventi ricevuti dalla view (es. click sui bottoni)
    private final CommandHandler commandHandler;
    private int gridSize;
    private JButton[][] buttonGrid;
    private JTextField[][] inputGrid;
    private final InputFilter documentFilter = new InputFilter();

    public GridPanel(GridInterface grid, CommandHandler commandHandler) {
        this.grid = grid;
        this.gridSize = grid.getSize();
        this.buttonGrid = new JButton[gridSize][gridSize];
        this.inputGrid = new JTextField[gridSize][gridSize];
        this.commandHandler = commandHandler;
        grid.addGridListener(this);
    }

    public JButton[][] getButtonGrid() {
        return buttonGrid;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void gridChanged(GridEvent e) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        int n = e.getSource().getSize();
        if(e.isNewGrid()) { // se le dimensioni della griglia sono cambiate, ricostruisco interamente il pannello
            this.gridSize = n;
            this.buttonGrid = new JButton[n][n];
            this.inputGrid = new JTextField[n][n];
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
            int minRow = Integer.MAX_VALUE;
            int minCol = Integer.MAX_VALUE;
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

                        // individuo la cella più in alto a sinistra del blocco, dove inserire il risultato
                        // da ottenere combinando le cifre del blocco
                        if(i < minRow && j < minCol){
                            minRow = i;
                            minCol = j;
                        }
                    }
                }
            }
            String operation = null;
            switch(e.getOperation()) {
                case SUM: operation = "+"; break;
                case SUBTRACTION: operation = "-"; break;
                case MULTIPLICATION: operation = "*"; break;
                case DIVISION: operation = "/"; break;
            }
            JButton targetResultButton = buttonGrid[minRow][minCol];
            targetResultButton.setText(e.getResult()+operation);
            targetResultButton.setFont(targetResultButton.getFont().deriveFont(Font.BOLD, 20));
            targetResultButton.setHorizontalAlignment(SwingConstants.LEFT);
            targetResultButton.setVerticalAlignment(SwingConstants.NORTH);
            targetResultButton.setUI(new MetalButtonUI() {
                protected Color getDisabledTextColor() {
                    return Color.BLACK;
                }
            });
        }
        if(e.isCageCleared()) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    inputGrid[i][j].setText("");
                }
            }
        }
        if(e.isConstraintChecked()) {
            List<Square> duplicateSquares = e.getDuplicateSquares();
            List<Square> invalidTargetResult = e.getInvalidTargetResultSquares();
            for(Square s : duplicateSquares) {
                inputGrid[s.getRow()][s.getColumn()].setForeground(Color.RED);
            }
            for(Square s : invalidTargetResult) {
                buttonGrid[s.getRow()][s.getColumn()].setUI(new MetalButtonUI() {
                    protected Color getDisabledTextColor() {
                        return Color.RED;
                    }
                });
            }
        }
        if(e.isNumberInserted()) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    inputGrid[i][j].setForeground(Color.BLACK);
                    buttonGrid[i][j].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.BLACK;
                        }
                    });
                }
            }
        }
        if(e.isSolutionRequested()) {
            int[][] currentSolution = e.getSource().getCurrentSolution();
            if(currentSolution != null) {
                for (int i = 0; i < gridSize; i++) {
                    for (int j = 0; j < gridSize; j++) {
                        inputGrid[i][j].setText("");
                        inputGrid[i][j].setText(currentSolution[i][j] + "");
                    }
                }
            }
            else {
                JOptionPane.showMessageDialog(topFrame,"There are no solutions for this game!",
                        "Unsolvable game",JOptionPane.ERROR_MESSAGE);
            }
        }
        if(e.isGridLoadedFromDisk()) {
            List<Grid.Cage> cageSchema = e.getSource().getCageSchema();
            for(Grid.Cage c : cageSchema) {
                boolean[][] cage = new boolean[n][n];
                for(Square s : c.getSquares()){
                    int i = s.getRow(); int j = s.getColumn();
                    cage[i][j] = true;
                }
                int minRow = Integer.MAX_VALUE;
                int minCol = Integer.MAX_VALUE;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if(cage[i][j]){
                            JButton button = buttonGrid[i][j];
                            button.setBackground(Color.WHITE);
                            button.setEnabled(false);
                            int top, left, bottom, right;
                            top = left = bottom = right = 4;
                            if(i > 0 && cage[i-1][j]) top = 1;
                            if(i < cage.length-1 && cage[i+1][j]) bottom = 1;
                            if(j > 0 && cage[i][j-1]) left = 1;
                            if(j < cage.length-1 && cage[i][j+1]) right = 1;
                            button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                            // individuo la cella più in alto a sinistra del blocco, dove inserire il risultato
                            // da ottenere combinando le cifre del blocco
                            if(i < minRow && j < minCol){
                                minRow = i;
                                minCol = j;
                            }
                        }
                    }
                }
                String operation = null;
                switch(c.getOperation()) {
                    case SUM: operation = "+"; break;
                    case SUBTRACTION: operation = "-"; break;
                    case MULTIPLICATION: operation = "*"; break;
                    case DIVISION: operation = "/"; break;
                }
                JButton targetResultButton = buttonGrid[minRow][minCol];
                targetResultButton.setText(c.getResult()+operation);
                targetResultButton.setFont(targetResultButton.getFont().deriveFont(Font.BOLD, 20));
                targetResultButton.setHorizontalAlignment(SwingConstants.LEFT);
                targetResultButton.setVerticalAlignment(SwingConstants.NORTH);
                targetResultButton.setUI(new MetalButtonUI() {
                    protected Color getDisabledTextColor() {
                        return Color.BLACK;
                    }
                });
            }
            startGameView();
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
                square.setLayout(new BoxLayout(square,BoxLayout.Y_AXIS));
                square.add(Box.createRigidArea(new Dimension(0,25)));
                square.setBackground(Color.WHITE);
                square.setOpaque(true);

                buttonGrid[i][j] = square;
                add(square);
            }
        }
    }

    public void startGameView() {
        int n = gridSize;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                JTextField input = new JTextField();
                input.setFont(input.getFont().deriveFont(Font.PLAIN, 35));
                input.setHorizontalAlignment(JTextField.CENTER);
                input.setBorder(BorderFactory.createEmptyBorder());
                ((AbstractDocument) input.getDocument()).setDocumentFilter(documentFilter);

                int row = i;
                int column = j;
                // ascolta inserimenti e rimozioni all'interno del JTextField
                input.getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent documentEvent) {
                        int val = Integer.parseInt(input.getText());
                        commandHandler.handle(new InsertNumberCommand(grid,row,column,val));
                    }

                    @Override
                    public void removeUpdate(DocumentEvent documentEvent) {
                        commandHandler.handle(new InsertNumberCommand(grid,row,column,0));
                    }

                    @Override
                    public void changedUpdate(DocumentEvent documentEvent) {}
                });

                buttonGrid[i][j].add(input, BorderLayout.CENTER);
                inputGrid[i][j] = input;
            }
        }
        revalidate();
    }

    public String getFilePath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("myGame.kenken"));
        chooser.setFileFilter(new FileNameExtensionFilter(".kenken files","kenken"));
        if(chooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            String filePath = chooser.getSelectedFile().getAbsolutePath();
            if (!filePath .endsWith(".kenken"))
                filePath += ".kenken";
                return filePath;
        }
        return null;
    }

    public void showIOErrorDialog() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(topFrame,"The file selected may be damaged or incompatible", "An error occurred trying to load the game",JOptionPane.ERROR_MESSAGE);
    }

    class InputFilter extends DocumentFilter {
        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attr) throws BadLocationException {
            if(text.equals("")) {
                fb.getDocument().remove(0,1); // svuota il JTextField
                return;
            }
            int documentLength = fb.getDocument().getLength();
            if (documentLength - length + text.length() <= 1)
                fb.insertString(offset, text.replaceAll("[^1-"+gridSize+"]", ""), attr);
        }
    }

}

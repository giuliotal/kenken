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
    private JToggleButton[][] buttonGrid;
    private JTextField[][] inputGrid;
    private final InputFilter documentFilter = new InputFilter();

    public GridPanel(GridInterface grid, CommandHandler commandHandler) {
        this.grid = grid;
        this.gridSize = grid.getSize();
        this.buttonGrid = new JToggleButton[gridSize][gridSize];
        this.inputGrid = new JTextField[gridSize][gridSize];
        this.commandHandler = commandHandler;
        grid.addGridListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public void gridChanged(GridEvent e) {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        int n = e.getSource().getSize();

        if(e.isNewGrid()) { // se le dimensioni della griglia sono cambiate, ricostruisco interamente il pannello
            rebuildGrid(n);
            updateCageSchema(e.getSource().getCageSchema(), n);
        }
        if(e.isCageCreated()) {
            updateCageSchema(e.getSource().getCageSchema(), n);
            resetSelection();
        }
        if(e.isCageCleared()) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    inputGrid[i][j].setText("");
                }
            }
        }
        if(e.isConstraintChecked()) {
            List<Grid.Square> duplicateSquares = e.getSource().getDuplicateSquares();
            List<Grid.Cage> invalidTargetResultCages = e.getSource().getInvalidTargetResultCages();
            for(Grid.Square s : duplicateSquares) {
                inputGrid[s.getRow()][s.getColumn()].setForeground(Color.RED);
            }
            // tra le celle che appartengono ai blocchi che non soddisfano il vincolo aritmetico, individuo
            // soltanto quella che contiene il vincolo in alto a sinistra, e lo rendo di colore rosso
            int minRow = Integer.MAX_VALUE;
            int minCol = Integer.MAX_VALUE;
            for(Grid.Cage c : invalidTargetResultCages){
                Grid.Square targetResultSquare = null;
                for(Grid.Square s : c.getSquares()) {
                    int i = s.getRow();
                    int j = s.getColumn();
                    if(i < minRow && j < minCol) {
                        minRow = i;
                        minCol = j;
                        targetResultSquare = s;
                    }
                }
            }
            buttonGrid[minRow][minCol].setUI(new MetalButtonUI() {
                protected Color getDisabledTextColor() {
                    return Color.RED;
                }
            });
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
        if(e.isNumberDeleted()) {
            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    if(e.getSource().getGrid()[i][j] == 0) inputGrid[i][j].setText("");
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
                JOptionPane.showMessageDialog(mainFrame,"There are no solutions for this game!",
                        "Unsolvable game",JOptionPane.ERROR_MESSAGE);
            }
        }

        repaint();
        revalidate();
    }

    private void rebuildGrid(int n) {
        this.gridSize = n;
        this.buttonGrid = new JToggleButton[n][n];
        this.inputGrid = new JTextField[n][n];
        this.removeAll();
        this.setLayout(new GridLayout(gridSize, gridSize));
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JToggleButton square = new JToggleButton();
                square.setLayout(new BoxLayout(square,BoxLayout.Y_AXIS));
                square.add(Box.createRigidArea(new Dimension(0,25)));
                square.setBackground(Color.WHITE);
                square.setOpaque(true);

                buttonGrid[i][j] = square;
                add(square);
            }
        }
    }

    private void updateCageSchema(List<Grid.Cage> cageSchema, int n) {
        for(Grid.Cage c : cageSchema) {
            boolean[][] cage = new boolean[n][n];
            for(Grid.Square s : c.getSquares()){
                int i = s.getRow(); int j = s.getColumn();
                cage[i][j] = true;
            }
            int minRow = Integer.MAX_VALUE;
            int minCol = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if(cage[i][j]){
                        setButtonBorders(buttonGrid[i][j], i, j, cage);
                        // individuo la cella più in alto a sinistra del blocco, dove inserire il risultato
                        // da ottenere combinando le cifre del blocco
                        if(i < minRow && j < minCol){
                            minRow = i;
                            minCol = j;
                        }
                    }
                }
            }
            setCageTargetResult(c.getResult(), c.getOperation(), minRow, minCol);
        }
    }

    private void setButtonBorders(JToggleButton button, int i, int j, boolean[][] selectedSquares) {
        button.setEnabled(false);
        int top, left, bottom, right;
        top = left = bottom = right = 4;
        if(i > 0 && selectedSquares[i-1][j]) top = 1;
        if(i < selectedSquares.length-1 && selectedSquares[i+1][j]) bottom = 1;
        if(j > 0 && selectedSquares[i][j-1]) left = 1;
        if(j < selectedSquares.length-1 && selectedSquares[i][j+1]) right = 1;
        button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
    }

    private void setCageTargetResult(int result, Grid.MathOperation operation, int i, int j) {
        String op = null;
        switch(operation) {
            case SUM: op = "+"; break;
            case SUBTRACTION: op = "-"; break;
            case MULTIPLICATION: op = "*"; break;
            case DIVISION: op = "/"; break;
        }
        JToggleButton targetResultButton = buttonGrid[i][j];
        targetResultButton.setText(result+op);
        targetResultButton.setFont(targetResultButton.getFont().deriveFont(Font.BOLD, 20));
        targetResultButton.setHorizontalAlignment(SwingConstants.LEFT);
        targetResultButton.setVerticalAlignment(SwingConstants.NORTH);
        targetResultButton.setUI(new MetalButtonUI() {
            protected Color getDisabledTextColor() {
                return Color.BLACK;
            }
        });
    }

    private void resetSelection() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                buttonGrid[i][j].setSelected(false);
            }
        }
    }

    public boolean checkSelection() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        boolean buttonSelected = false;
        for (int i = 0; i < gridSize && !buttonSelected; i++) {
            for (int j = 0; j < gridSize && !buttonSelected; j++) {
                if(buttonGrid[i][j].isSelected()) buttonSelected = true;
            }
        }
        if(!buttonSelected) {
            JOptionPane.showMessageDialog(mainFrame, "At least one square must be selected",
                    "Invalid cage selection", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        boolean[][] selectedSquares = getSelectedSquares();
        if(!grid.verifyAdjacency(selectedSquares)) {
            JOptionPane.showMessageDialog(mainFrame,"Square selected have to be adjacent in order to bulid a cage.\n" +
                    "Please change selection.", "Invalid cage selection",JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean[][] getSelectedSquares() {
        boolean[][] selectedSquares = new boolean[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if(buttonGrid[i][j].isSelected()) selectedSquares[i][j] = true;
            }
        }
        return selectedSquares;
    }

    public int getLockedSquares() {
        int lockedSquares = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if(!buttonGrid[i][j].isEnabled())
                    lockedSquares++;
            }
        }
        return lockedSquares;
    }

    public int getTargetResult() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        boolean targetResultObtained = false;
        int result = 0;
        do {
            String input = JOptionPane.showInputDialog(mainFrame,"Enter the target result:");
            if(input==null)
                return -1;
            try {
                result = Integer.parseInt(input);
                targetResultObtained = true;
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(mainFrame,"Please insert an integer value.", "Invalid target result",JOptionPane.ERROR_MESSAGE);
            }
        }while(!targetResultObtained);
        return result;
    }

    public Grid.MathOperation getOperation() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        boolean operationObtained = false;
        String operation = null;
        do {
            String input = JOptionPane.showInputDialog(mainFrame,"Enter the math operation:");
            if(input==null)
                return null;
            if(input.matches("[\\+\\-\\*/]")) {
                operation = input;
                operationObtained = true;
            }
            else {
                JOptionPane.showMessageDialog(mainFrame,"Please insert one of the allowed operator symbols: +, -, *, /.",
                        "Invalid operator symbol",JOptionPane.ERROR_MESSAGE);
            }
        }while(!operationObtained);

        Grid.MathOperation mathOperation = null;
        if(operation != null){
            switch(operation){
                case "+": mathOperation = Grid.MathOperation.SUM; break;
                case "*": mathOperation = Grid.MathOperation.MULTIPLICATION; break;
                case "-": mathOperation = Grid.MathOperation.SUBTRACTION; break;
                case "/": mathOperation = Grid.MathOperation.DIVISION; break;
            }
        }
        return mathOperation;
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
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(mainFrame,"The file selected may be damaged or incompatible", "An error occurred trying to load the game",JOptionPane.ERROR_MESSAGE);
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

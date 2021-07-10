package mvc.view;

import command.CommandHandler;
import mvc.model.exceptions.SolutionsNotFoundException;
import mvc.gridCommand.InsertNumberCommand;
import mvc.model.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

// Concrete Observer (VIEW)
public class GridPanel extends JPanel implements GridListener {

    // subject
    private final GridInterface grid;
    private final CommandHandler commandHandler;

    private int gridSize;
    private JToggleButton[][] buttonGrid;
    private JTextField[][] inputGrid;

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

        if(e.isNewGrid()) {
            rebuildGrid(n);
        }
        if(e.isSchemaUpdated()) { // caricamento/ripristino di una griglia di gioco o di un blocco
            repaintCageSchema(e.getSource().getCageSchema(), n);
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
            List<Square> duplicateSquares = e.getSource().getDuplicateSquares();
            List<Cage> invalidTargetResultCages = e.getSource().getInvalidTargetResultCages();
            // i numeri duplicati vengono evidenziati in rosso
            for(Square s : duplicateSquares) {
                inputGrid[s.getRow()][s.getColumn()].setForeground(Color.RED);
            }
            // tra le celle che appartengono ai blocchi che non soddisfano il vincolo aritmetico, individuo
            // soltanto quella che contiene il vincolo in alto a sinistra, e lo rendo di colore rosso
            if(!invalidTargetResultCages.isEmpty()) {
                for (Cage c : invalidTargetResultCages) {
                    int minRow = Integer.MAX_VALUE;
                    int minCol = Integer.MAX_VALUE;
                    for (Square s : c.getSquares()) {
                        int i = s.getRow();
                        int j = s.getColumn();
                        if (i < minRow && j < minCol) {
                            minRow = i;
                            minCol = j;
                        }
                    }
                    buttonGrid[minRow][minCol].setUI(new MetalButtonUI() {
                        protected Color getDisabledTextColor() {
                            return Color.RED;
                        }
                    });
                }
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
            try {
                int[][] currentSolution = e.getSource().getCurrentSolution();
                if (currentSolution != null) {
                    for (int i = 0; i < gridSize; i++) {
                        for (int j = 0; j < gridSize; j++) {
                            inputGrid[i][j].setText("");
                            inputGrid[i][j].setText(currentSolution[i][j] + "");
                        }
                    }
                }
            } catch(SolutionsNotFoundException ex){
                showSolutionsNotFoundDialog();
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

                JTextField input = new JTextField();
                input.setFont(input.getFont().deriveFont(Font.PLAIN, 35));
                input.setDisabledTextColor(Color.BLACK);
                input.setHorizontalAlignment(JTextField.CENTER);
                input.setBorder(BorderFactory.createEmptyBorder());
                InputFilter documentFilter = new InputFilter(gridSize);
                ((AbstractDocument) input.getDocument()).setDocumentFilter(documentFilter);

                buttonGrid[i][j].add(input, BorderLayout.CENTER);
                inputGrid[i][j] = input;
                input.setVisible(false);
                input.setEnabled(false);
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
            }
        }
    }

    private void repaintCageSchema(List<Cage> cageSchema, int n) {
        boolean[][] currentSchema = new boolean[n][n];
        for(Cage c : cageSchema) {
            boolean[][] cage = new boolean[n][n];
            for(Square s : c.getSquares()){
                int i = s.getRow();
                int j = s.getColumn();
                int val = s.getValue();
                cage[i][j] = true;
                currentSchema[i][j] = true;
                inputGrid[i][j].setText(val == 0 ? "" : val + "");
            }
            int minRow = Integer.MAX_VALUE;
            int minCol = Integer.MAX_VALUE;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if(cage[i][j]){
                        paintButtonBorders(buttonGrid[i][j], i, j, cage);
                        // individuo la cella più in alto a sinistra del blocco, dove inserire il risultato
                        // da ottenere combinando le cifre del blocco
                        if(i < minRow && j < minCol){
                            minRow = i;
                            minCol = j;
                        }
                    }
                }
            }
            paintCageTargetResult(c.getResult(), c.getOperation(), minRow, minCol);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(!currentSchema[i][j]) {
                    buttonGrid[i][j].updateUI();
                    buttonGrid[i][j].setBorder(UIManager.getBorder("Button.border"));
                    buttonGrid[i][j].setText("");
                    buttonGrid[i][j].setEnabled(true);
                    inputGrid[i][j].setVisible(false);
                }
            }
        }
    }

    private void paintButtonBorders(JToggleButton button, int i, int j, boolean[][] selectedSquares) {
        button.setEnabled(false);
        inputGrid[i][j].setVisible(true);
        int top, left, bottom, right;
        top = left = bottom = right = 4;
        if(i > 0 && selectedSquares[i-1][j]) top = 1;
        if(i < selectedSquares.length-1 && selectedSquares[i+1][j]) bottom = 1;
        if(j > 0 && selectedSquares[i][j-1]) left = 1;
        if(j < selectedSquares.length-1 && selectedSquares[i][j+1]) right = 1;
        button.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));
    }

    private void paintCageTargetResult(int result, MathOperation operation, int i, int j) {
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

    public boolean[][] getSelectedSquares() {
        boolean[][] selectedSquares = new boolean[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if(buttonGrid[i][j].isSelected()) selectedSquares[i][j] = true;
            }
        }
        return selectedSquares;
    }

    public void showSelectionError() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(mainFrame, "At least one square must be selected",
                "Invalid cage selection", JOptionPane.ERROR_MESSAGE);
    }

    public void showAdjacencyError() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(mainFrame,"Square selected have to be adjacent in order to bulid a cage.\n" +
                "Please change selection.", "Invalid cage selection",JOptionPane.ERROR_MESSAGE);
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

    public MathOperation getOperation() {
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

        MathOperation mathOperation = null;
        switch(operation){
            case "+": mathOperation = MathOperation.SUM; break;
            case "*": mathOperation = MathOperation.MULTIPLICATION; break;
            case "-": mathOperation = MathOperation.SUBTRACTION; break;
            case "/": mathOperation = MathOperation.DIVISION; break;
        }
        return mathOperation;
    }

    public void startGameView() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JTextField input = inputGrid[i][j];
                input.setEnabled(true);
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

    public int getMaxSolutions() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        int maxSolutions = 0;
        boolean validInput = false;
        do {
            String input = JOptionPane.showInputDialog(topFrame, "Enter a maximum value of solutions to display:");
            if (input == null)
                return -1;
            try {
                maxSolutions = Integer.parseInt(input);
                validInput = true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(topFrame, "Please insert an integer value.", "Invalid input", JOptionPane.ERROR_MESSAGE);
            }
        }while(!validInput);
        return maxSolutions;
    }


    public void showSolutionsNotFoundDialog() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(mainFrame,"There are no solutions for this game!",
                "Unsolvable game",JOptionPane.ERROR_MESSAGE);
    }

    public void showLoadErrorDialog() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(mainFrame,"The file selected may be damaged or incompatible", "An error occurred trying to load the game",JOptionPane.ERROR_MESSAGE);
    }

    public void showSaveErrorDialog() {
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JOptionPane.showMessageDialog(mainFrame,"Cannot save an empty game!", "An error occurred trying to save the game",JOptionPane.ERROR_MESSAGE);
    }

    public boolean showNewGameDialog() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        int ret = JOptionPane.showConfirmDialog(topFrame,
                "Are you sure you want to create a new game?\n" +
                        "All unsaved progress will be lost.", "Are you sure?", JOptionPane.YES_NO_OPTION);
        return ret == JOptionPane.YES_OPTION;
    }

    static class InputFilter extends DocumentFilter {

        private final int gridSize;

        public InputFilter(int gridSize) { this.gridSize = gridSize; }

        public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attr) throws BadLocationException {
            int documentLength = fb.getDocument().getLength();
            if (documentLength - length + text.length() <= 1) {
                fb.getDocument().remove(0,documentLength); // svuota il JTextField
                fb.insertString(offset, text.replaceAll("[^1-" + gridSize + "]", ""), attr);
            }
        }
    }

}

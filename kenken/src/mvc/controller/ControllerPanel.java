package mvc.controller;

import command.CommandHandler;
import command.HistoryCommandHandler;
import mvc.model.GridEvent;
import mvc.model.GridInterface;
import mvc.model.GridListener;
import mvc.view.CreateCageAction;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ControllerPanel extends JPanel implements GridListener {

    private final CommandHandler commandHandler;
    private final GridInterface subject;
    // aggiungo un riferimento anche alla view per modificarne i bottoni
    private final GridPanel gridPanel;

    private final JPanel controlCommands;

    private final JPanel historyCommands;

    private final JPanel createCageCommand;
    private final JButton createCageButton;

    private final JPanel startGameCommand;
    private final JButton startGameButton;

    private final JPanel gameCommands;
    private final JButton checkConstraintsButton;
    private final JButton clearGridButton;
    private final JButton showSolutionsButton;

    private final JPanel solutionNavigationCommands;
    private final JButton previousSolutionButton;
    private final JButton nextSolutionButton;

    public ControllerPanel(GridInterface grid, CommandHandler commandHandler, GridPanel gridPanel) {
        this.subject = grid;
        this.gridPanel = gridPanel;
        this.commandHandler = commandHandler;
        grid.addGridListener(this);

        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        controlCommands = new JPanel(new GridLayout(3,1,0,10));

        historyCommands = new JPanel(new GridLayout(1,2));

        JButton undo = new JButton("UNDO");
        undo.addActionListener(evt -> commandHandler.handle(HistoryCommandHandler.NonExecutableCommands.UNDO));
        undo.setEnabled(false);
        historyCommands.add(undo);

        JButton redo = new JButton("REDO");
        redo.addActionListener(evt -> commandHandler.handle(HistoryCommandHandler.NonExecutableCommands.REDO));
        redo.setEnabled(false);
        historyCommands.add(redo);

        createCageCommand = new JPanel();

        createCageButton = new JButton(new CreateCageAction(subject, commandHandler, gridPanel));
        createCageButton.setText("Create cage");
        createCageButton.setEnabled(false);
        createCageCommand.add(createCageButton);

        startGameCommand = new JPanel();

        startGameButton = new JButton(new StartGameAction(grid, gridPanel, this, commandHandler));
        startGameButton.setText("START GAME");
        startGameButton.setEnabled(false);
        startGameCommand.add(startGameButton);

        controlCommands.add(historyCommands);
        controlCommands.add(createCageCommand);
        controlCommands.add(startGameCommand);

        // ------------------------------------------------

        gameCommands = new JPanel(new GridLayout(4,1,0,10));

        checkConstraintsButton = new JButton(new CheckConstraintsAction(grid, commandHandler)); //checkConstraintsAction
        checkConstraintsButton.setText("Check constraints");
        checkConstraintsButton.setEnabled(false);

        clearGridButton = new JButton(new ClearGridAction(grid,commandHandler));
        clearGridButton.setText("Clear grid");
        clearGridButton.setEnabled(false);

        showSolutionsButton = new JButton(new ShowSolutionsAction(grid, this, commandHandler));
        showSolutionsButton.setText("Show solutions");
        showSolutionsButton.setEnabled(false);

        solutionNavigationCommands = new JPanel(new GridLayout(1,2,0,10));
        previousSolutionButton = new JButton(new NavigateSolutionsAction(grid,commandHandler,-1));
        previousSolutionButton.setText("Previous");
        previousSolutionButton.setEnabled(false);
        solutionNavigationCommands.add(previousSolutionButton);

        nextSolutionButton = new JButton(new NavigateSolutionsAction(grid,commandHandler,1));
        nextSolutionButton.setText("Next");
        nextSolutionButton.setEnabled(false);
        solutionNavigationCommands.add(nextSolutionButton);

        gameCommands.add(checkConstraintsButton);
        gameCommands.add(clearGridButton);
        gameCommands.add(showSolutionsButton);
        gameCommands.add(solutionNavigationCommands);

        add(Box.createRigidArea(new Dimension(0,20)));
        add(controlCommands);
        add(Box.createRigidArea(new Dimension(0,10)));
        add(gameCommands);
        add(Box.createRigidArea(new Dimension(0,20)));

    }

    public void enableControlButtons() {
        for(Component c : historyCommands.getComponents()) c.setEnabled(true);
        createCageButton.setEnabled(true);
    }

    public int getMaxSolutions() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(gridPanel);
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

    public void setCreateCageButton(boolean b) { createCageButton.setEnabled(b); }

    public void setStartGameButton(boolean b) {
        startGameButton.setEnabled(b);
    }

    public void setCheckConstraintsButton(boolean b) { checkConstraintsButton.setEnabled(b);}

    public void setClearGridButton(boolean b) { clearGridButton.setEnabled(b); }

    public void setShowSolutionsButton(boolean b) { showSolutionsButton.setEnabled(b);}

    @Override
    public void gridChanged(GridEvent e) {
        // verifico se tutte le celle appartengono ad un blocco, in caso affermativo può partire il gioco
        // inoltre non è più possibile creare nuovi blocchi
        if(e.isCageCreated()) {
            int n = e.getSource().getSize();
            int lockedSquares = gridPanel.getLockedSquares();
            if(lockedSquares == n*n){
                startGameButton.setEnabled(true);
                createCageButton.setEnabled(false);
            }
        }
        if(e.isSolutionRequested()) {
            previousSolutionButton.setEnabled(e.getSource().hasPreviousSolution());
            nextSolutionButton.setEnabled(e.getSource().hasNextSolution());
        }
        repaint();
        revalidate();
    }
}

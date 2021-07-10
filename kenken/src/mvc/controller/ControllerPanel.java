package mvc.controller;

import command.CommandHandler;
import command.HistoryCommandHandler;
import mvc.gridCommand.*;
import mvc.model.*;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

public class ControllerPanel extends JPanel implements GridListener {

    private final GridPanel gridPanel;

    private final JButton undo;
    private final JButton redo;

    private final JButton createCageButton;

    private final JButton startGameButton;

    private final JButton checkConstraintsButton;
    private final JButton clearGridButton;
    private final JButton showSolutionsButton;

    private final JButton previousSolutionButton;
    private final JButton nextSolutionButton;

    private boolean gameStarted = false;

    public ControllerPanel(GridInterface grid, CommandHandler commandHandler, GridPanel gridPanel) {
        this.gridPanel = gridPanel;
        grid.addGridListener(this);

        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

        JPanel controlCommands = new JPanel(new GridLayout(3, 1, 0, 10));

        JPanel historyCommands = new JPanel(new GridLayout(1, 2));

        undo = new JButton("UNDO");
        undo.addActionListener(evt -> commandHandler.handle(HistoryCommandHandler.NonExecutableCommands.UNDO));
        undo.setEnabled(false);
        historyCommands.add(undo);

        redo = new JButton("REDO");
        redo.addActionListener(evt -> commandHandler.handle(HistoryCommandHandler.NonExecutableCommands.REDO));
        redo.setEnabled(false);
        historyCommands.add(redo);

        JPanel createCageCommand = new JPanel();

        createCageButton = new JButton("Create cage");
        createCageButton.addActionListener(evt -> {
            // verifica la selezione effettuata dall'utente
            boolean[][] selectedSquares = gridPanel.getSelectedSquares();
            boolean buttonSelected = false;
            for (int i = 0; i < selectedSquares.length && !buttonSelected; i++)
                for (int j = 0; j < selectedSquares.length && !buttonSelected; j++)
                    if(selectedSquares[i][j]) buttonSelected = true;

            if(!buttonSelected) {
                gridPanel.showErrorDialog("Invalid cage selection",
                        "At least one square must be selected" );
                return;
            }
            if(!Cage.verifyAdjacency(selectedSquares)) {
                gridPanel.showErrorDialog("Invalid cage selection",
                        "Square selected have to be adjacent in order to bulid a cage.\nPlease change selection.");
                return;
            }

            int result = gridPanel.getTargetResultInput();
            if(result == -1) return;

            MathOperation operation = gridPanel.getOperationInput();
            if(operation == null) return;

            List<Square> squares = new LinkedList<>();
            for (int i = 0; i < selectedSquares.length; i++) {
                for (int j = 0; j < selectedSquares.length; j++) {
                    if(selectedSquares[i][j]) squares.add(new Square(i,j));
                }
            }
            commandHandler.handle(new CreateCageCommand(grid, squares.toArray(new Square[0]), result, operation));
        });
        createCageButton.setEnabled(false);
        createCageCommand.add(createCageButton);

        JPanel startGameCommand = new JPanel();

        startGameButton = new JButton("START GAME");
        startGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                gridPanel.startGameView();
                startGameButton.setEnabled(false);
                checkConstraintsButton.setEnabled(true);
                clearGridButton.setEnabled(true);
                showSolutionsButton.setEnabled(true);
            }
        });
        startGameButton.setEnabled(false);
        startGameCommand.add(startGameButton);

        controlCommands.add(historyCommands);
        controlCommands.add(createCageCommand);
        controlCommands.add(startGameCommand);

        JPanel gameCommands = new JPanel(new GridLayout(4, 1, 0, 10));

        checkConstraintsButton = new JButton("Check constraints");
        checkConstraintsButton.addActionListener(evt -> commandHandler.handle(new CheckConstraintsCommand(grid)));
        checkConstraintsButton.setEnabled(false);
        gameCommands.add(checkConstraintsButton);

        clearGridButton = new JButton("Clear grid");
        clearGridButton.addActionListener(evt -> commandHandler.handle(new ClearGridCommand(grid)));
        clearGridButton.setEnabled(false);
        gameCommands.add(clearGridButton);

        showSolutionsButton = new JButton("Show solutions");
        showSolutionsButton.addActionListener(evt -> new ShowSolutionsCommand(grid, gridPanel));
        showSolutionsButton.setEnabled(false);
        gameCommands.add(showSolutionsButton);

        JPanel solutionNavigationCommands = new JPanel(new GridLayout(1, 2, 0, 10));

        previousSolutionButton = new JButton("Previous");
        previousSolutionButton.addActionListener(evt -> commandHandler.handle(new NavigateSolutionsCommand(grid, -1)));
        previousSolutionButton.setEnabled(false);
        solutionNavigationCommands.add(previousSolutionButton);

        nextSolutionButton = new JButton("Next");
        previousSolutionButton.addActionListener(evt -> commandHandler.handle(new NavigateSolutionsCommand(grid, 1)));
        nextSolutionButton.setEnabled(false);
        solutionNavigationCommands.add(nextSolutionButton);
        gameCommands.add(solutionNavigationCommands);

        add(Box.createRigidArea(new Dimension(0,20)));
        add(controlCommands);
        add(Box.createRigidArea(new Dimension(0,10)));
        add(gameCommands);
        add(Box.createRigidArea(new Dimension(0,20)));

    }

    public void setCreateCageButton(boolean b) { createCageButton.setEnabled(b); }

    public void setStartGameButton(boolean b) { startGameButton.setEnabled(b); }

    public void setCheckConstraintsButton(boolean b) { checkConstraintsButton.setEnabled(b);}

    public void setClearGridButton(boolean b) { clearGridButton.setEnabled(b); }

    public void setShowSolutionsButton(boolean b) { showSolutionsButton.setEnabled(b);}

    @Override
    public void gridChanged(GridEvent e) {
        if(e.isNewGrid()) {
            gameStarted = false;
            undo.setEnabled(true);
            redo.setEnabled(true);
            startGameButton.setEnabled(false);
            previousSolutionButton.setEnabled(false);
            nextSolutionButton.setEnabled(false);
        }
        if(e.isSchemaUpdated()) {
            int n = e.getSource().getSize();
            int lockedSquares = gridPanel.getLockedSquares();
            if(lockedSquares == n*n && !gameStarted){
                gameStarted = true;
                startGameButton.setEnabled(true);
                createCageButton.setEnabled(false);
            }
            else {
                startGameButton.setEnabled(false);
                createCageButton.setEnabled(true);
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

package mvc.controller;

import command.CommandHandler;
import mvc.model.GridEvent;
import mvc.model.GridInterface;
import mvc.model.GridListener;
import mvc.view.CreateCageAction;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;

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

    public ControllerPanel(GridInterface grid, CommandHandler commandHandler, GridPanel gridPanel) {
        this.subject = grid;
        this.gridPanel = gridPanel;
        this.commandHandler = commandHandler;
        grid.addGridListener(this);

//        this.setLayout(new GridLayout(3,1, 0, 10));
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
//        this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));

        controlCommands = new JPanel(new GridLayout(3,1,0,10));

        historyCommands = new JPanel(new GridLayout(1,2));

        JButton undo = new JButton("UNDO");
        // undo.addActionListener()
        undo.setEnabled(false);
        historyCommands.add(undo);
        JButton redo = new JButton("REDO");
        // redo.addActionListener()
        redo.setEnabled(false);
        historyCommands.add(redo);

        createCageCommand = new JPanel();

        createCageButton = new JButton(new CreateCageAction(subject, commandHandler, gridPanel));
        createCageButton.setText("Create cage");
        createCageButton.setEnabled(false);
        createCageCommand.add(createCageButton);

        startGameCommand = new JPanel();

        startGameButton = new JButton(new StartGameAction(commandHandler, gridPanel, this));
        startGameButton.setText("START GAME");
        startGameButton.setEnabled(false);
        startGameCommand.add(startGameButton);

        controlCommands.add(historyCommands);
        controlCommands.add(createCageCommand);
        controlCommands.add(startGameCommand);

        // ------------------------------------------------

        gameCommands = new JPanel(new GridLayout(3,1,0,10));

        checkConstraintsButton = new JButton(new CheckConstraintsAction(grid, commandHandler)); //checkConstraintsAction
        checkConstraintsButton.setText("Check constraints");
        checkConstraintsButton.setEnabled(false);

        clearGridButton = new JButton(new ClearGridAction(grid,commandHandler));
        clearGridButton.setText("Clear grid");
        clearGridButton.setEnabled(false);

        showSolutionsButton = new JButton("Show solutions");
        showSolutionsButton.setEnabled(false);

        gameCommands.add(checkConstraintsButton);
        gameCommands.add(clearGridButton);
        gameCommands.add(showSolutionsButton);

        add(controlCommands);
        add(Box.createRigidArea(new Dimension(0,20)));
        add(gameCommands);

    }

    public void enableControlButtons() {
        for(Component c : historyCommands.getComponents()) c.setEnabled(true);
        createCageButton.setEnabled(true);
    }

    public JButton getCreateCageButton() {
        return createCageButton;
    }

    public void setStartGameButtonEnabled(boolean b) {
        startGameButton.setEnabled(b);
    }

    public void setClearGridButton(boolean b) { clearGridButton.setEnabled(b); }

    @Override
    public void gridChanged(GridEvent e) {
        // verifico se tutte le celle appartengono ad un blocco, in caso affermativo può partire il gioco
        // inoltre non è più possibile creare nuovi blocchi
        if(e.isCageCreated()) {
            int n = e.getSource().getSize();
            int lockedSquares = e.getSource().getLockedSquares();
            if(lockedSquares == n*n){
                startGameButton.setEnabled(true);
                createCageButton.setEnabled(false);
                checkConstraintsButton.setEnabled(true);
                clearGridButton.setEnabled(true);
                showSolutionsButton.setEnabled(true);
            }
        }
        repaint();
        revalidate();
    }
}

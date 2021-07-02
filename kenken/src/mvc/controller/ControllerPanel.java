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

    private final JPanel historyCommands;

    private final JPanel cageCreationCommand;
    private final JButton createCageButton;

    private final JPanel gameCommands;
    private final JButton startGameButton;

    public ControllerPanel(GridInterface grid, CommandHandler commandHandler, GridPanel gridPanel) {
        this.subject = grid;
        this.gridPanel = gridPanel;
        this.commandHandler = commandHandler;
        grid.addGridListener(this);

        this.setLayout(new GridLayout(3,1, 0, 10));
        this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));

        historyCommands = new JPanel(new GridLayout(1,2));

        JButton undo = new JButton("UNDO");
        // undo.addActionListener()
        undo.setEnabled(false);
        historyCommands.add(undo);
        JButton redo = new JButton("REDO");
        // redo.addActionListener()
        redo.setEnabled(false);
        historyCommands.add(redo);

        // ------------------------------------------------

        cageCreationCommand = new JPanel();

        createCageButton = new JButton(new CreateCageAction(subject, commandHandler, gridPanel));
        createCageButton.setText("Create cage");
        createCageButton.setEnabled(false);
        cageCreationCommand.add(createCageButton);

        // ------------------------------------------------

        gameCommands = new JPanel();

        startGameButton = new JButton(new StartGameAction(commandHandler, gridPanel, this));
        startGameButton.setText("START GAME");
        startGameButton.setEnabled(false);
        gameCommands.add(startGameButton);

        add(historyCommands);
        add(cageCreationCommand);
        add(gameCommands);

    }

    public void enableButtons() {
        for(Component c : historyCommands.getComponents()) c.setEnabled(true);
        createCageButton.setEnabled(true);
    }

    public JButton getCreateCageButton() {
        return createCageButton;
    }

    public JButton getStartGameButton() {
        return startGameButton;
    }

    @Override
    public void gridChanged(GridEvent e) {
        // verifico se tutte le celle appartengono ad un blocco, in caso affermativo può partire il gioco
        // inoltre non è più possibile creare nuovi blocchi
        int n = e.getSource().getSize();
        int lockedSquares = e.getSource().getLockedSquares();
        if(lockedSquares == n*n){
            startGameButton.setEnabled(true);
            createCageButton.setEnabled(false);
        }
        else if(lockedSquares < n*n) {
            startGameButton.setEnabled(false);
            createCageButton.setEnabled(true);
        }
        repaint();
        revalidate();
    }
}

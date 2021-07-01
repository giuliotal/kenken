package mvc.controller;

import command.CommandHandler;
import mvc.model.GridInterface;
import mvc.view.CreateCageAction;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;

public class GridController extends JPanel {

    private final CommandHandler commandHandler;
    private final GridInterface subject;
    // aggiungo un riferimento anche alla view per modificarne i bottoni
    private final GridPanel panel;

    private final JPanel historyCommands;
    private final JPanel cageCreationCommands;

    public GridController(GridInterface gridInterface, CommandHandler commandHandler, GridPanel panel) {
        this.subject = gridInterface;
        this.panel = panel;
        this.commandHandler = commandHandler;

        this.setLayout(new GridLayout(2,1, 0, 10));

        historyCommands = new JPanel(new GridLayout(1,2));

        JButton undo = new JButton("UNDO");
        // undo.addActionListener()
        historyCommands.add(undo);
        JButton redo = new JButton("REDO");
        // redo.addActionListener()
        historyCommands.add(redo);

        // ------------------------------------------------

        cageCreationCommands = new JPanel();

        JButton createCage = new JButton(new CreateCageAction(subject, commandHandler, panel));
        createCage.setText("Create cage");
        cageCreationCommands.add(createCage);

        add(historyCommands);
        add(cageCreationCommands);
    }
}

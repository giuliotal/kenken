import command.HistoryCommandHandler;
import mvc.controller.ControllerPanel;
import mvc.gridCommand.CreateGridCommand;
import mvc.gridCommand.LoadGameCommand;
import mvc.gridCommand.SaveGameCommand;
import mvc.model.Grid;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

import javax.swing.*;
import java.awt.*;

public class MainGUI {
    public static void main(String[] args) {

        JFrame frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();

        final GridInterface grid = new Grid();

        final HistoryCommandHandler commandHandler = new HistoryCommandHandler();

        final GridPanel gridPanel = new GridPanel(grid, commandHandler);
        gridPanel.setPreferredSize(new Dimension(500, 500));

        final ControllerPanel controllerPanel = new ControllerPanel(grid, commandHandler, gridPanel);

        JMenu newGame = new JMenu("New game");
        for(int i = 3; i<=6; i++){
            JMenuItem newGrid = new JMenuItem(i+"x"+i);
            int finalI = i;
            newGrid.addActionListener(evt -> commandHandler.handle(new CreateGridCommand(grid, gridPanel, controllerPanel, finalI)));
            newGame.add(newGrid);
        }
        menuBar.add(newGame);

        JMenu saveOrLoad = new JMenu("Save/load");

        JMenuItem saveGame = new JMenuItem("Save game");
        saveGame.addActionListener(evt -> commandHandler.handle(new SaveGameCommand(grid, gridPanel)));
        saveOrLoad.add(saveGame);

        JMenuItem loadGame = new JMenuItem("Load game");
        loadGame.addActionListener(evt -> commandHandler.handle(new LoadGameCommand(grid, gridPanel, controllerPanel)));
        saveOrLoad.add(loadGame);

        menuBar.add(saveOrLoad);

        JMenu help = new JMenu("Help");
        JMenuItem howToPlay = new JMenuItem("How to play");
        JMenuItem rules = new JMenuItem("KenKen rules");
        help.add(rules);
        help.add(howToPlay);

        menuBar.add(help);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
        contentPane.add(Box.createRigidArea(new Dimension(20,0)));
        contentPane.add(controllerPanel);
        contentPane.add(Box.createRigidArea(new Dimension(20,0)));
        contentPane.add(new JSeparator(JSeparator.VERTICAL));
        contentPane.add(Box.createRigidArea(new Dimension(20,0)));
        contentPane.add(gridPanel);
        contentPane.add(Box.createRigidArea(new Dimension(20,0)));

        frame.setTitle("Kenken");
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(contentPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

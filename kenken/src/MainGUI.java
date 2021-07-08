import command.HistoryCommandHandler;
import mvc.controller.ControllerPanel;
import mvc.model.Grid;
import mvc.model.GridInterface;
import mvc.view.CreateGridAction;
import mvc.view.GridPanel;
import mvc.view.LoadGameAction;
import mvc.view.SaveGameAction;

import javax.swing.*;
import java.awt.*;

public class MainGUI {
    public static void main(String[] args) {

        JFrame frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();

        final GridInterface grid = new Grid();

        final HistoryCommandHandler handler = new HistoryCommandHandler();
        // i bottoni undo e redo li inserisco nel gridController e gli passo come handler l'historyCommandHandler)

        final GridPanel gridPanel = new GridPanel(grid, handler);
        gridPanel.setPreferredSize(new Dimension(500, 500));

        final ControllerPanel controllerPanel = new ControllerPanel(grid, handler, gridPanel);

        JMenu newGame = new JMenu("New game");
        for(int i = 3; i<=6; i++){
            JMenuItem newGrid = new JMenuItem(new CreateGridAction(grid, gridPanel, controllerPanel, handler, i));
            newGame.add(newGrid);
        }
        menuBar.add(newGame);

        JMenu saveOrLoad = new JMenu("Save/load");
        JMenuItem saveGame = new JMenuItem(new SaveGameAction(grid, gridPanel, handler));
        saveGame.setText("Save game");
        JMenuItem loadGame = new JMenuItem(new LoadGameAction(grid, gridPanel, controllerPanel, handler));
        loadGame.setText("Load game");
        saveOrLoad.add(saveGame);
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

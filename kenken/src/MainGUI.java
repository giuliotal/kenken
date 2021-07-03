import command.HistoryCommandHandler;
import mvc.controller.ControllerPanel;
import mvc.model.Grid;
import mvc.model.GridInterface;
import mvc.view.CreateGridAction;
import mvc.view.GridPanel;

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

        JMenu howToPlay = new JMenu("How to play");
        JMenuItem rules = new JMenuItem("KenKen rules");
        JMenuItem help = new JMenuItem("Help");
        howToPlay.add(rules);
        howToPlay.add(help);

        menuBar.add(howToPlay);

        JPanel contentPane = new JPanel(new FlowLayout(FlowLayout.LEFT,20,0));
        contentPane.add(controllerPanel);
        contentPane.add(gridPanel);

        frame.setTitle("Kenken");
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(contentPane, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

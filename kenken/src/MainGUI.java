import javax.swing.*;
import java.awt.*;

public class MainGUI {
    public static void main(String[] args) {

        JFrame frame = new JFrame();
        JMenuBar menuBar = new JMenuBar();

        JMenu newGame = new JMenu("Nuova partita");
        for(int i = 3; i<=6; i++){
            JMenuItem dimensioneGriglia = new JMenuItem(i+"x"+i);
            newGame.add(dimensioneGriglia);
        }
        menuBar.add(newGame);

        JMenu info = new JMenu("Info");
        menuBar.add(info);

        frame.setTitle("Kenken");
        frame.setJMenuBar(menuBar);
//        frame.getContentPane().add(controlPanel, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}

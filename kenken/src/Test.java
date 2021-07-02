import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        JFrame f = new JFrame();
        JPanel panel = new JPanel(new GridLayout(4,4));
        JButton button = new JButton("22+");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setEnabled(false);
//        JLabel label = new JLabel("22+");
//        button.add(label);


        f.getContentPane().add(panel);
        panel.add(button);
        f.pack();
        f.setVisible(true);

    }
}

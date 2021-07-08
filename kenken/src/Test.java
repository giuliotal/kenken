import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Test {

    public static void main(String[] args) throws InterruptedException {
        JFrame f = new JFrame();
        JPanel panel = new JPanel(new GridLayout(4,4));
        JButton button = new JButton("22+");
        JTextField input = new JTextField("ENZO");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                input.setText("");
            }
        });
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setEnabled(true);
        input.setEnabled(false);


        f.getContentPane().add(panel);
        panel.add(button);
        panel.add(input);
        f.pack();
        f.setVisible(true);

    }
}

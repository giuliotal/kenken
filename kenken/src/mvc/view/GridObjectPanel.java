package mvc.view;

import mvc.model.GridEvent;
import mvc.model.GridListener;

import javax.swing.*;

// Concrete Observer
public class GridObjectPanel extends JComponent implements GridListener {

    @Override
    public void gridChanged(GridEvent e) {
        repaint();
        revalidate();
    }
}

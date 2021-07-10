package mvc.model;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGrid implements GridInterface {

    private final List<GridListener> listeners = new LinkedList<>();

    @Override
    public void addGridListener(GridListener l) {
        if (listeners.contains(l))
            return;
        listeners.add(l);
    }

    @Override
    public void removeGridListener(GridListener l) {
        listeners.remove(l);
    }

    protected void notifyListeners(GridEvent e) {
        for (GridListener gol : listeners)
            gol.gridChanged(e);
    }
}

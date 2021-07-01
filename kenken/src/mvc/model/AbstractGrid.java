package mvc.model;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractGrid implements GridInterface {

    private final List<GridListener> listeners = new LinkedList<>();

    @Override
    // attach(Observer)
    public void addGridListener(GridListener l) {
        // scorre linearmente sulla lista, si potrebbe ottimizzare utilizzando un'altra struttura dati per memorizzare gli observers
        // N.B. gli observers vengono memorizzati direttamente, non c'è nessun ChangeManager che funge da Mediator
        if (listeners.contains(l))
            return;
        listeners.add(l);
    }

    @Override
    // detach(Observer)
    public void removeGridListener(GridListener l) {
        listeners.remove(l);
    }

    protected void notifyListeners(GridEvent e) {
        for (GridListener gol : listeners)
            gol.gridChanged(e);
    }
}

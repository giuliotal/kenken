package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.model.Memento;

public class ClearGridCommand implements Command {

    private final GridInterface grid;
    private final Memento gridMemento;

    public ClearGridCommand(GridInterface grid) {
        this.grid = grid;
        this.gridMemento = grid.getMemento();
    }

    @Override
    public boolean doIt() {
        grid.clear();
        return true;
    }

    @Override
    public boolean undoIt() {
        grid.setMemento(gridMemento);
        return true;
    }
}

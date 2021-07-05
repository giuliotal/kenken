package mvc.gridCommand;

import command.Command;
import mvc.model.GridInterface;
import mvc.view.GridPanel;

public class SaveGameCommand implements Command {

    private final GridInterface grid;
    private final GridPanel gridPanel;

    public SaveGameCommand(GridInterface grid, GridPanel gridPanel) {
        this.grid = grid;
        this.gridPanel = gridPanel;
    }

    @Override
    public boolean doIt() {
        String filePath = gridPanel.getFilePath();
        if(filePath != null) {
            if(!grid.save(filePath)) gridPanel.showIOErrorDialog();
            return true;
        }
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

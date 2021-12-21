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
        String filePath = gridPanel.getFilePathInput(GridPanel.FILE_OP.SAVE_OP);
        if(filePath != null) {
            if(!grid.save(filePath))
                gridPanel.showErrorDialog("An error occurred trying to save the game",
                        "Cannot save an empty game!");
            return true;
        }
        return false;
    }

    @Override
    public boolean undoIt() {
        return false;
    }
}

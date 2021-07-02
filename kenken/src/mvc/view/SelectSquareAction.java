package mvc.view;

import command.CommandHandler;
import mvc.gridCommand.SelectSquareCommand;
import mvc.model.GridInterface;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SelectSquareAction extends AbstractAction {

    GridInterface grid;
    GridPanel panel;
    CommandHandler commandHandler;

    public SelectSquareAction(GridInterface grid, GridPanel panel, CommandHandler commandHandler){
        this.grid = grid;
        this.panel = panel;
        this.commandHandler = commandHandler;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        commandHandler.handle(new SelectSquareCommand(grid, panel, actionEvent));
    }
}

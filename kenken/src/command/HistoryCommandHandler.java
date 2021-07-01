package command;

import java.util.LinkedList;

public class HistoryCommandHandler implements CommandHandler {

	//sono comandi speciali in quanto non è possibile eseguire su di essi doIt e undoIt, essendo essi stessi undo e redo
	public enum NonExecutableCommands implements Command {
		UNDO, REDO;

		@Override
		public boolean doIt() {
			throw new NoSuchMethodError();
		}

		@Override
		public boolean undoIt() {
			throw new NoSuchMethodError();
		}
	}

	private int maxHistoryLength = 100;

	private final LinkedList<Command> history = new LinkedList<>();

	private final LinkedList<Command> redoList = new LinkedList<>();

	public HistoryCommandHandler() {
		this(100);
	}

	public HistoryCommandHandler(int maxHistoryLength) {
		if (maxHistoryLength < 0)
			throw new IllegalArgumentException();
		this.maxHistoryLength = maxHistoryLength;
	}

	public void handle(Command cmd) {
		if (cmd == NonExecutableCommands.UNDO) {
			undo();
			return;
		}
		if (cmd == NonExecutableCommands.REDO) {
			redo();
			return;
		}
		if (cmd.doIt()) {
			// restituisce true: può essere annullato
			addToHistory(cmd);
		} else {
			// restituisce false: non può essere annullato
			history.clear();
		}
		if (redoList.size() > 0)
			redoList.clear();
	}

	private void redo() {
		if (redoList.size() > 0) {
			Command redoCmd = redoList.removeFirst();
			redoCmd.doIt();
			history.addFirst(redoCmd);

		}
	}

	private void undo() {
		if (history.size() > 0) {
			Command undoCmd = history.removeFirst();
			undoCmd.undoIt();
			redoList.addFirst(undoCmd);
		}
	}

	private void addToHistory(Command cmd) {
		history.addFirst(cmd);
		if (history.size() > maxHistoryLength) {
			history.removeLast();
		}

	}

}

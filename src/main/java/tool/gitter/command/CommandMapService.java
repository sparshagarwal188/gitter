package tool.gitter.command;

import tool.gitter.model.Action;

import java.util.HashMap;
import java.util.Map;

public class CommandMapService {

    public static final Map<Action, Class<? extends Command>> actionCommandMap;

    static {
        actionCommandMap = new HashMap<>();
        actionCommandMap.put(Action.commit, CommitCommand.class);
        actionCommandMap.put(Action.add, AddCommand.class);
        actionCommandMap.put(Action.init, InitCommand.class);
        actionCommandMap.put(Action.help, HelpCommand.class);
        actionCommandMap.put(Action.log, LogCommand.class);
        actionCommandMap.put(Action.diff, DiffCommand.class);
        actionCommandMap.put(Action.status, StatusCommand.class);
        actionCommandMap.put(Action.reset, ResetCommand.class);
    }

    public static Class<? extends Command> getCommandClass(Action action) {
        return actionCommandMap.get(action);
    }
}

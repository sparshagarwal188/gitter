package tool.gitter.command;

import org.apache.commons.cli.*;
import tool.gitter.model.Action;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HelpCommand extends Command {

    private final CommandLine commandLine;
    public HelpCommand(Action action, String[] ar) {
        super(action);
        commandLine = initCommandLine(ar);
    }

    private CommandLine initCommandLine(String[] ar) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        getOptions().forEach(options::addOption);
        return initCommandLine(ar, parser, options);
    }

    private List<Option> getOptions() {
        return new ArrayList<>();
    }

    @Override
    public String execute() {
        String[] args = commandLine.getArgs();
        if(args.length == 1) {
            return getCommandDescriptions();
        }
        String commandArgument = args[1];

        Optional<Action> actionOptional = Action.getAction(commandArgument);
        if(actionOptional.isEmpty()) {
            return "invalid command argument! please pass valid command in 'gitter help <command>'\n" +
                    "for list of valid commands use 'gitter help'\n";
        }
        Action action = actionOptional.get();
        return action.getDocumentation();
    }

    private String getCommandDescriptions() {
        StringBuilder sb = new StringBuilder("\navailable gitter commands are -\n\n");
        for(Action action : Action.values()) {
            if(action != Action.help) {
                sb.append(action.name()).append(":- ").append(action.getDescription()).append("\n\n");
            }
        }
        return sb.toString();
    }
}

package tool.gitter.command;

import org.apache.commons.cli.*;
import tool.gitter.hash.HashApplier;
import tool.gitter.model.Action;
import tool.gitter.model.Commit;

import java.util.ArrayList;
import java.util.List;

public class ResetCommand extends Command {

    private HashApplier hashApplier;
    private final CommandLine commandLine;

    public ResetCommand(Action action, String[] ar) throws Exception {
        super(action);
        commandLine = initCommandLine(ar);
        hashApplier = new HashApplier("SHA-1");
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
        checkInitialization();
        setObjectPersister(this.userDirectory);

        String exp = commandLine.getArgs()[1];
        int n = Integer.parseInt(exp.substring(exp.indexOf('~') + 1));

        String head = getLatestCommitHash();

        for(;n>0; n--) {
            Commit lastCommit = (Commit) objectPersister.returnObject(head);
            head = lastCommit.getParentHash();
        }

        setLatestCommithash(head);
        stageCurrentFiles();
        return head;
    }

    private void stageCurrentFiles() {
        AddCommand addCommand = new AddCommand(action, commandLine, hashApplier, objectPersister, userDirectory);
        addCommand.indexFiles("");
    }
}

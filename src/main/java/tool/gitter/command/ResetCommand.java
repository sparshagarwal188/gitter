package tool.gitter.command;

import org.apache.commons.cli.CommandLine;
import tool.gitter.hash.HashApplier;
import tool.gitter.model.Action;
import tool.gitter.model.Commit;

public class ResetCommand extends Command {

    private HashApplier hashApplier;

    public ResetCommand(Action action, CommandLine commandLine) throws Exception {
        super(action, commandLine);
        hashApplier = new HashApplier("SHA-1");
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

package tool.gitter.command;

import org.apache.commons.cli.CommandLine;
import tool.gitter.model.Action;
import tool.gitter.model.Commit;

public class LogCommand extends Command {
    public LogCommand(Action action, CommandLine commandLine) {
        super(action, commandLine);
    }

    public String execute() {
        checkInitialization();
        setObjectPersister(this.userDirectory);

        int count = 1;
        if(commandLine.hasOption("n")) {
            try {
                count = Integer.parseInt(commandLine.getOptionValue("n"));
            } catch (Exception e) {
                System.out.println("Invalid argument value passed for [-n] <args>\n" +
                        "please enter integer value\n\n");
            }
        } else {
            System.out.println("\nplease provide the number of logs to print with 'gitter log -n <number>'\n" +
                    "for details see documentation using 'gitter help log'\n" +
                    "printing present commit details -");
        }

        String headCommitHash = getLatestCommitHash();
        return extractCommitsAndCreateResponse(headCommitHash, count);
    }

    private String extractCommitsAndCreateResponse(String headCommitHash, int count) {
        StringBuilder sb = new StringBuilder();
        String currentCommitHash = headCommitHash;

        while(currentCommitHash != null && count-- > 0) {
            Commit commit = (Commit)objectPersister.returnObject(currentCommitHash);
            if(commit == null) {
                break;
            }
            sb.append(commit.toDisplayString(currentCommitHash));
            currentCommitHash = commit.getParentHash();
        }
        return sb.toString();
    }
}

package tool.gitter.command;

import org.apache.commons.cli.*;
import tool.gitter.model.Action;
import tool.gitter.model.Commit;

import java.util.List;

public class LogCommand extends Command {
    private final CommandLine commandLine;
    public LogCommand(Action action, String[] ar) {
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
        Option messageOption = Option.builder("n")
                .argName("numberOfLogs")
                .hasArg()
                .desc("Number of logs from HEAD backwards (inclusive)")
                .longOpt("number")
                .build();

        return List.of(messageOption);
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

    private static List<Option> getLogOptions() {
        Option messageOption = Option.builder("n")
                .argName("numberOfLogs")
                .hasArg()
                .desc("Number of logs from HEAD backwards (inclusive)")
                .longOpt("number")
                .build();

        return List.of(messageOption);
    }
}

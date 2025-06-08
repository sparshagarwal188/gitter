package tool.gitter.command;

import org.apache.commons.cli.*;
import tool.gitter.model.Action;
import tool.gitter.utils.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static tool.gitter.model.Constants.GITTER;
import static tool.gitter.model.Constants.OBJECTS;

public class InitCommand extends Command {

    private final CommandLine commandLine;
    public InitCommand(Action action, String[] ar) {
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
        initDirectory();
        return "\ninitialized empty gitter repository in " +
                Paths.get(this.userDirectory.toString(), GITTER);
    }

    private void initDirectory() {
        try {
            Path gitFolder = FileUtil.createFolder(this.userDirectory, GITTER);

            FileUtil.createFolder(gitFolder, OBJECTS);
        } catch (Exception e) {
            System.out.println("\ncould not initialize gitter repository\n" + e.getMessage());
        }
    }
}

package tool.gitter.command;

import org.apache.commons.cli.CommandLine;
import tool.gitter.model.Action;
import tool.gitter.utils.FileUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

import static tool.gitter.model.Constants.GITTER;
import static tool.gitter.model.Constants.OBJECTS;

public class InitCommand extends Command {

    public InitCommand(Action action, CommandLine commandLine) {
        super(action, commandLine);
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

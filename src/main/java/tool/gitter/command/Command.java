package tool.gitter.command;

import org.apache.commons.cli.CommandLine;
import tool.gitter.model.Action;
import tool.gitter.dao.FileObjectPersister;
import tool.gitter.exception.ApplicationException;
import tool.gitter.model.*;
import tool.gitter.utils.FileUtil;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static tool.gitter.model.Constants.*;

public abstract class Command {

    protected final Action action;
    protected final CommandLine commandLine;
    protected Path userDirectory;
    protected FileObjectPersister objectPersister;

    public Command(Action action, CommandLine commandLine) {
        this.action = action;
        this.commandLine = commandLine;
        setDirectory();
    }

    public static Command get(Action action, CommandLine commandLine) throws ApplicationException {
        Class<? extends Command> commandClazz = CommandMapService.getCommandClass(action);
        try {
            return commandClazz.getDeclaredConstructor(Action.class, CommandLine.class).newInstance(action, commandLine);
        } catch (Exception e) {
            System.out.println("failed to execute command!\n" +
                    "cause : " + e.getMessage() + "\n" +
                    "use 'gitter help' for details on command usage..\n\n");
            throw new ApplicationException("execution failure", e);
        }
    }

    public abstract String execute();

    protected Map<String, String> createCommittedFilesToHashStringMap(Commit lastCommit) {
        Map<String, String> filePathToHashStringMap = new HashMap<>();
        visitFilePath(lastCommit.getContentTreeHash(), filePathToHashStringMap);
        return filePathToHashStringMap;
    }

    protected void visitFilePath(String directoryHash, Map<String, String> filePathToHashString) {
        Directory directory = (Directory) objectPersister.returnObject(directoryHash);

        if (directory != null && directoryHash != null) {
            filePathToHashString.put(directory.getPath(), directoryHash);
            filePathToHashString.putAll(directory.getFilePathToHashStringMap());

            directory.getDirectoryPathToHashStringMap().values()
                    .forEach(hash -> visitFilePath(hash, filePathToHashString));
        }
    }

    protected Map<String, String> createIndexFilesToHashStringMap() {
        Index index = fetchIndex();
        if(index == null) {
            return new HashMap<>();
        }
        return index.getIndex().stream()
                .collect(Collectors.toMap(Index.IndexedFile::getFilepath, Index.IndexedFile::getObjectHash));
    }
    protected void setDirectory() {
        String directory = FileUtil.getPresentWorkingDirectory();
        if(this.userDirectory == null) {
            this.userDirectory = Path.of(directory);
        }
    }

    protected void checkInitialization() {
        try {
            if(!FileUtil.fileExists(Path.of(userDirectory.toString(), GITTER))) {
                System.out.println("error : gitter repository not initialized\n" +
                        "please use 'gitter init' to initialize\n");
                throw new ApplicationException();
            }
        } catch (Exception e) {
            throw new ApplicationException();
        }
    }

    protected void setObjectPersister(Path path) {
        if(this.objectPersister == null) {
            this.objectPersister = new FileObjectPersister(path);
        }
    }

    protected boolean stagingAreaEmpty() throws ApplicationException {
        Path pathToIndex = Paths.get(userDirectory.toString(), GITTER, INDEX);
        try {
            return !FileUtil.fileExists(pathToIndex);
        } catch (Exception e) {
            System.out.println("could not access index area!\n" +
                    "error : " + e.getMessage() + "\n" +
                    "please ensure that gitter repository exists and is reachable\n");
            throw new ApplicationException();
        }
    }

    protected HEAD fetchHead() {
        Path pathToHead = Paths.get(userDirectory.toString(), GITTER, HEAD);
        try {
            return (HEAD) FileUtil.deserialize(pathToHead);
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            System.out.println("could not fetch head!\n" +
                    "error : " + e.getMessage() + "\n" +
                    "please ensure that gitter repository exists and is reachable\n");
            throw new ApplicationException();
        }
    }

    protected String getLatestCommitHash() {
        HEAD head = fetchHead();
        return head != null ? head.getLatestCommitHash() : null;
    }

    protected void saveHead(HEAD head) {
        Path pathToHead = Paths.get(userDirectory.toString(), GITTER, HEAD);
        try {
            FileUtil.saveObject(head, pathToHead);
        } catch (Exception e) {
            System.out.println("could not save latest commit info!\n" +
                    "error : " + e.getMessage() + "\n" +
                    "please add files to index and commit again\n");
        }
    }
    protected void setLatestCommithash(String commitId) {
        HEAD head = new HEAD(commitId);
        saveHead(head);
    }

    protected Index fetchIndex() {
        try {
            return (Index)FileUtil.deserialize(Paths.get(userDirectory.toString(), GITTER, INDEX));
        } catch (FileNotFoundException e) {
            return null;
        } catch (Exception e) {
            System.out.println("could not access index area!\n" +
                    "error : " + e.getMessage() + "\n" +
                    "please ensure that gitter repository exists and is reachable\n");
            throw new ApplicationException();
        }
    }
}

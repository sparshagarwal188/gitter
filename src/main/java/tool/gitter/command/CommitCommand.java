package tool.gitter.command;

import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import tool.gitter.model.Action;
import tool.gitter.hash.HashApplier;
import tool.gitter.model.Commit;
import tool.gitter.model.Directory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static tool.gitter.model.Constants.*;

public class CommitCommand extends Command {

    private final HashApplier hashApplier;
    private final CommandLine commandLine;

    public CommitCommand(Action action, String[] ar) throws NoSuchAlgorithmException {
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
        Option messageOption = Option.builder("m")
                .argName("commitMessage")
                .hasArg()
                .desc("A message to describe the commit")
                .longOpt("message")
                .build();

        Option addFilesToIndexOption = Option.builder("a")
                .desc("add all files to index area before commit")
                .longOpt("all")
                .build();

        return List.of(messageOption, addFilesToIndexOption);
    }

    @Override
    public String execute() {
        checkInitialization();
        setObjectPersister(this.userDirectory);

        String[] messageArguments = commandLine.getOptionValues("m");
        if(messageArguments == null) {
            return "commit command without option[-m]\n" +
                    "Please provide commit message\n" +
                    "or\n" +
                    "use 'gitter help' for command usage and options";
        }
        String message = String.join("\n", messageArguments);

        if(commandLine.hasOption("a")) {
            AddCommand addCommand = new AddCommand(action, commandLine, hashApplier, objectPersister, userDirectory);
            addCommand.indexFiles("");
        }

        if(!stagingAreaEmpty()) {
            try {
                String commitId = createAndSaveCommitObject(message);
                setLatestCommithash(commitId);
            } catch (Exception e) {
                return "error occurred while committing!\n" +
                        "error : " + e.getMessage() + "\n" +
                        "please retry after fixing the error\n" +
                        "or\n" +
                        "use 'gitter help' for documentation.\n\n";
            }
//            Commit co = (Commit) objectPersister.returnObject(commitId);
//            System.out.printf("commit message %s\n", co.getMessage());
        }
        clearIndex();
        return "";
    }

    private void clearIndex() {
        Path pathToIndex = Paths.get(userDirectory.toString(), GITTER, INDEX);
        try {
            Files.deleteIfExists(pathToIndex);
        } catch (Exception e) {
            System.out.println("index seems to be corrupt.\nPlease unstage all changes before proceeding!!\n" +
                    "please use 'gitter help' for detailed documentation");
        }
    }

    private String createAndSaveCommitObject(String message) {
        Commit commitObject = createCommitObject(message);
        String commitObjectHash = calculateCommitObjectHash(commitObject);
        saveCommitObject(commitObject, commitObjectHash);
        return commitObjectHash;
    }

    private Commit createCommitObject(String message) {
        String latestCommitHash = getLatestCommitHash();
        String contentTreeHash = createHashRecursively(userDirectory.toFile(), latestCommitHash);

        return Commit.builder()
                .message(message)
                .parentHash(latestCommitHash)
                .author(System.getProperty("user.name"))
                .contentTreeHash(contentTreeHash)
                .date(new Date())
                .build();
    }
    private String createHashRecursively(File file, String latestCommitHash) {
        Map<String, String> indexFilePathToFileHashStringMap = createIndexFilesToHashStringMap();

        Commit latestCommit = (Commit)objectPersister.returnObject(latestCommitHash);
        Directory commitDirectory = null;
        if(latestCommit != null) {
            commitDirectory = (Directory)objectPersister.returnObject(latestCommit.getContentTreeHash());
        }

        return createHashRecursively(file, commitDirectory, indexFilePathToFileHashStringMap);
    }
    @SneakyThrows
    private String createHashRecursively(File currentLocation, Directory correspondingCommitDir, Map<String, String> indexFilePathToHashStringMap) {
        Directory newDirectory = new Directory();
        if(correspondingCommitDir == null) {
            correspondingCommitDir = Directory.emptyDirectory();
        }
        File[] files = currentLocation.listFiles();

        if(files != null) {
            for(File file : files) {
                BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

                if(basicFileAttributes.isRegularFile()) {
                    String fileHash;
                    if(indexFilePathToHashStringMap.containsKey(file.getAbsolutePath())) {
                        fileHash = indexFilePathToHashStringMap.get(file.getAbsolutePath());
                    } else {
                        fileHash = correspondingCommitDir.getFilePathToHashStringMap().get(file.getAbsolutePath());
                    }
                    saveFileInformationInNewDirectory(fileHash, file, newDirectory);
                }
                else if(basicFileAttributes.isDirectory()) {
                    Directory commitSubDirectory = (Directory) objectPersister.returnObject(
                            correspondingCommitDir.getDirectoryPathToHashStringMap().get(file.getAbsolutePath()));

                    String subDirectoryHash = createHashRecursively(file, commitSubDirectory, indexFilePathToHashStringMap);
                    newDirectory.getDirectoryPathToHashStringMap().put(file.getAbsolutePath(), subDirectoryHash);
                }
            }
        }

        String newDirectoryHash = generateHashFromChildHash(newDirectory);
        objectPersister.saveObject(newDirectory, newDirectoryHash);
        return newDirectoryHash;
    }

    private void saveFileInformationInNewDirectory(String fileHash, File file, Directory newDirectory) {
        if(fileHash != null) {
            newDirectory.getFilePathToHashStringMap().put(file.getAbsolutePath(), fileHash);
        }
    }

    private String generateHashFromChildHash(Directory baseDirectory) {
        List<String> directoryHash = new ArrayList<>(baseDirectory.getDirectoryPathToHashStringMap().values());
        Collections.sort(directoryHash);

        List<String> fileHash = new ArrayList<>(baseDirectory.getFilePathToHashStringMap().values());
        Collections.sort(fileHash);

        return hashApplier.calculateHash(
                hashApplier.calculateHash(directoryHash.toString())
                        .concat(hashApplier.calculateHash(fileHash.toString()))
        );
    }

    private String calculateCommitObjectHash(Commit commitObject) {
        String commitMetaDataHash = hashApplier.calculateHash(commitObject.toString());
        return hashApplier.calculateHash(String.join(EMPTY, commitObject.getContentTreeHash(), commitMetaDataHash));
    }

    @SneakyThrows
    private void saveCommitObject(Commit commitObject, String commitObjectHash) {
        this.objectPersister.saveObject(commitObject, commitObjectHash);
    }
}

package tool.gitter.command;

import org.apache.commons.cli.CommandLine;
import tool.gitter.model.Action;
import tool.gitter.hash.HashApplier;
import tool.gitter.model.Commit;
import tool.gitter.model.Index;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;

public class StatusCommand extends Command {

    private HashApplier hashApplier;
    public StatusCommand(Action action, CommandLine commandLine) throws NoSuchAlgorithmException {
        super(action, commandLine);
        hashApplier = new HashApplier("SHA-1");
    }

    public String execute() {
        checkInitialization();
        setObjectPersister(this.userDirectory);

        StringBuilder sb = new StringBuilder();
        createResponse(sb);
        return sb.toString();
    }

    private void createResponse(StringBuilder sb) {
        addIndexedFileDescription(sb);
        addUnstagedFiles(sb);
    }

    private void addUnstagedFiles(StringBuilder sb) {
        sb.append("\nchanges not staged for commmit :\n");
        Commit lastCommit = (Commit) objectPersister.returnObject(getLatestCommitHash());
        if(lastCommit == null) {
            sb.append("No commit history found. Please create commit for list of files modified since last commit but not yet staged\n\n");
            return;
        }

        Map<String, String> committedFileToHashStringMap = createCommittedFilesToHashStringMap(lastCommit);
        Map<String, String> stagedFiles = createIndexFilesToHashStringMap();
        checkAndAddFilesForDisplay(stagedFiles.keySet(), committedFileToHashStringMap, sb, userDirectory);
    }

    private void checkAndAddFilesForDisplay(Set<String> stagedFiles, Map<String, String> commitPathToHashStringMap,
                                            StringBuilder sb, Path path) {
        File directory = path.toFile();
        File[] files = directory.listFiles();

        if(files != null) {
            for (File file : files) {
                try {
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    if (basicFileAttributes.isRegularFile()) {
                        processFile(file, stagedFiles, commitPathToHashStringMap, sb);
                    } else if (basicFileAttributes.isDirectory()) {
                        checkAndAddFilesForDisplay(stagedFiles, commitPathToHashStringMap, sb, file.toPath());
                    }
                } catch (Exception e) {
                    System.out.println("could not fetch attributes for file : " + file.getAbsolutePath() + "\n" +
                            "please ensure proper permissions and retry\n\n");
                }
            }
        }
    }

    private void processFile(File file, Set<String> stagedFiles, Map<String, String> commitPathToHashStringMap, StringBuilder sb) {
        if(!stagedFiles.contains(file.getAbsolutePath())) {
            String fileHash = hashApplier.calculateFileHash(file);

            if(!commitPathToHashStringMap.containsKey(file.getAbsolutePath()) ||
                    !fileHash.equals(commitPathToHashStringMap.get(file.getAbsolutePath()))) {

                sb.append(String.format("%s\n", file.getAbsolutePath()));
            }
        }
    }

    private void addIndexedFileDescription(StringBuilder sb) {
        sb.append("changes to be committed :\n");
        Index index = fetchIndex();
        if(index != null) {
            if (index.getIndex() != null && index.getIndex().size() > 0) {
                index.getIndex().forEach(file -> sb.append(String.format("%s\n", file.getFilepath())));
            } else {
                sb.append("No files added to index. use 'gitter add [options]' to add file to index\n");
            }
        } else {
            sb.append("No files added to index. use 'gitter add [options]' to add file to index\n");
        }
    }
}

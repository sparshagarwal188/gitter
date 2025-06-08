package tool.gitter.command;

import org.apache.commons.cli.*;
import tool.gitter.comparison.ComparisonProvider;
import tool.gitter.comparison.Diff_Match_Patch;
import tool.gitter.hash.HashApplier;
import tool.gitter.model.Action;
import tool.gitter.model.Commit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DiffCommand extends Command {

    private HashApplier hashApplier;
    private final CommandLine commandLine;
    public DiffCommand(Action action, String[] ar) throws NoSuchAlgorithmException {
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

        Path path;
        if(commandLine.getArgs().length > 1) {
            path = Path.of(commandLine.getArgs()[1]);
        } else {
            path = this.userDirectory;
        }

        return constructDifferenceDetails(path);
    }

    private String constructDifferenceDetails(Path path) {
        Map<String, String> committedFilesToHashStringMap = createCommittedFilesToHashStringMap();
        Map<String, String> indexFilesToHashStringMap = createIndexFilesToHashStringMap();

        List<String> unstagedChangedFile = new ArrayList<>();
        getUnstagedChangedFiles(path, committedFilesToHashStringMap, indexFilesToHashStringMap.keySet(), unstagedChangedFile);

        return processEligibleFiles(unstagedChangedFile, committedFilesToHashStringMap);
    }

    private Map<String, String> createCommittedFilesToHashStringMap() {
        String commitHash = getLatestCommitHash();
        Commit commit = (Commit)objectPersister.returnObject(commitHash);
        if(commit == null) {
            return new HashMap<>();
        }
        return createCommittedFilesToHashStringMap(commit);
    }

    private String processEligibleFiles(List<String> eligibleFiles, Map<String, String> commitDirectoryMap) {
        StringBuilder sb = new StringBuilder();
        for(String file : eligibleFiles) {
            try {
                File currFile = new File(file);
                String currContent = Files.readString(currFile.toPath());
                String oldContent = objectPersister.returnFileContents(commitDirectoryMap.get(file));

                LinkedList<Diff_Match_Patch.Diff> diffs = ComparisonProvider.instance().diff_main(oldContent, currContent);
                ComparisonProvider.instance().diff_cleanupSemantic(diffs);
                LinkedList<Diff_Match_Patch.Patch> patches = ComparisonProvider.instance().patch_make(diffs);
                String description = ComparisonProvider.instance().patch_toText(patches);

                sb.append(String.format("%s\n\n%s\n\n", currFile.getAbsolutePath(), description));
            } catch (Exception e) {
                System.out.println("could not process file contents for file!\n" +
                        "file : " + file + "\n" +
                        "error : " + e.getMessage() + "\n" +
                        "please fix the error and retry or try adding file to index via 'gitter add'\n\n");
            }
        }
        return sb.toString();
    }

    private void getUnstagedChangedFiles(Path path, Map<String, String> commitDirectoryMap, Set<String> stagedFiles, List<String> eligibleFiles) {
        File currFile = path.toFile();
        try {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(currFile.toPath(), BasicFileAttributes.class);
            if(basicFileAttributes.isRegularFile()) {
                addIfEligible(currFile, stagedFiles, commitDirectoryMap, eligibleFiles);
            } else if(basicFileAttributes.isDirectory()) {
                File[] files = currFile.listFiles();
                if(files != null) {
                    for (File file : files) {
                        getUnstagedChangedFiles(file.toPath(), commitDirectoryMap, stagedFiles, eligibleFiles);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("could not fetch attributes for file : " + currFile.getAbsolutePath() + "\n" +
                    "please ensure proper permissions and retry\n\n");
        }
    }

    private void addIfEligible(File file, Set<String> stagedFiles, Map<String, String> commitPathToHashStringMap, List<String> eligibleFiles) {
        if (!stagedFiles.contains(file.getAbsolutePath())) {
            String fileHash = hashApplier.calculateFileHash(file);

            if (!commitPathToHashStringMap.containsKey(file.getAbsolutePath()) ||
                    !fileHash.equals(commitPathToHashStringMap.get(file.getAbsolutePath()))) {

                eligibleFiles.add(file.getAbsolutePath());
            }
        }
    }
}

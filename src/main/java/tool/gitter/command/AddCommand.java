package tool.gitter.command;

import org.apache.commons.cli.*;
import tool.gitter.dao.FileObjectPersister;
import tool.gitter.exception.FileOperationException;
import tool.gitter.hash.HashApplier;
import tool.gitter.model.Action;
import tool.gitter.model.Index;
import tool.gitter.utils.FileUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static tool.gitter.model.Constants.GITTER;
import static tool.gitter.model.Constants.INDEX;

public class AddCommand extends Command {

    private final HashApplier hashApplier;
    private final CommandLine commandLine;
    public AddCommand(Action action, String[] ar) throws Exception {
        super(action);
        commandLine = initCommandLine(ar);
        hashApplier = new HashApplier("SHA-1");
    }
    public AddCommand(Action action, CommandLine commandLine, HashApplier hashApplier, FileObjectPersister objectPersister, Path userDirectory) {
        super(action);
        this.commandLine = commandLine;
        this.hashApplier = hashApplier;
        this.objectPersister = objectPersister;
        this.userDirectory = userDirectory;
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

        String[] args = commandLine.getArgs();
        if(args.length < 2) {
            return "error: \ninvalid arguments passed, please use 'gitter help status' for a list of available arguments\n";
        }
        if(".".equals(args[1])) {
            indexFiles("");
        } else {
            indexFiles(args[1].substring(args[1].indexOf('*') + 1));
        }
        return "\n";
    }

    public void indexFiles(String suffix) {
        List<File> allPresentFiles = new ArrayList<>();
        Index index = fetchIndex();
        if(index == null) {
            index = new Index();
        }

        FileUtil.getAllFilesRecursively(userDirectory.toFile(), allPresentFiles);

        for(File file : allPresentFiles) {
            if(file.getName().endsWith(".class")) {
                continue;
            }
            if(file.getAbsolutePath().endsWith(suffix)) {
                try {
                    Index.IndexedFile indexedFile = indexFile(file);
                    index.add(indexedFile);
                } catch (FileOperationException e) {
                    System.out.println("Could not index file : " + file.getAbsolutePath());
                    System.out.println(e.getMessage());
                }
            }
        }

        saveIndex(index);
    }

    private Index.IndexedFile indexFile(File file) throws FileOperationException {
        String hash = hashApplier.calculateFileHash(file);
        saveIndexedFile(file, hash);
        return new Index.IndexedFile(file.getName(), file.getAbsolutePath(), hash);
    }

    private void saveIndexedFile(File file, String hash) throws FileOperationException {
        objectPersister.saveFileObject(file, hash);
    }

    private void saveIndex(Index index) {
        try {
            Path path = Paths.get(userDirectory.toString(), GITTER, INDEX);
            objectPersister.saveObject(index, path);
        } catch (FileOperationException e) {
            System.out.println("Error saving index\nPlease try again...");
        }
//        Index i = (Index) objectPersister.returnObject(path);
//        System.out.printf("The sha of deserialized object is %s\n", i.getIndex().get(0).getObjectHash());
    }
}

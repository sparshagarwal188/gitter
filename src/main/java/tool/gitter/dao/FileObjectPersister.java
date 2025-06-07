package tool.gitter.dao;

import tool.gitter.exception.FileOperationException;
import tool.gitter.utils.FileUtil;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static tool.gitter.model.Constants.GITTER;
import static tool.gitter.model.Constants.OBJECTS;

public class FileObjectPersister {

    private final Path gitterParentPath;
    public FileObjectPersister(Path gitterParentDirectory) {
        this.gitterParentPath = gitterParentDirectory;
    }

    public void saveFileObject(File file, String hash) throws FileOperationException {
        try {
            Path filepath = getFilePathToObject(hash);
            if (!Files.exists(filepath)) {
                FileUtil.saveFile(filepath, file);
            }
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    public String returnFileContents(String hash) {
        if(hash == null) {
            return "";
        }
        Path pathToFile = getFilePathToObject(hash);
        try {
            return FileUtil.returnFileContents(pathToFile);
        } catch (Exception e) {
            System.out.printf("\nCould not fetch file contents from : %s\n%s\n\n", pathToFile.toString(), e.getMessage());
            return null;
        }
    }


    public <T extends Serializable> void saveObject(T object, Path path) throws FileOperationException {
        try {
            FileUtil.saveObject(object, path);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }

    public Object returnObject(Path path) {
        try {
            return FileUtil.deserialize(path);
        } catch (Exception e) {
            System.out.printf("\nCould not fetch object contents from : %s\n%s\n\n", path.toString(), e.getMessage());
            return null;
        }
    }


    public <T extends Serializable> void saveObject(T object, String hash) throws FileOperationException {
        try {
            Path path = getFilePathToObject(hash);
            FileUtil.saveObject(object, path);
        } catch (Exception e) {
            throw new FileOperationException(e);
        }
    }
    public Object returnObject(String hash) {
        if(hash == null) {
            return null;
        }
        Path pathToObject = getFilePathToObject(hash);
        try {
            return FileUtil.deserialize(pathToObject);
        } catch (Exception e) {
            System.out.printf("\nCould not fetch object contents from : %s\n%s\n\n", pathToObject.toString(), e.getMessage());
            return null;
        }
    }

    public boolean objectExists(String hash) {
        Path filePath = getFilePathToObject(hash);
        try {
            return FileUtil.fileExists(filePath);
        } catch (Exception e) {
            System.out.printf("\nCould not check existence of file : %s\n%s\n\n", filePath.toString(), e.getMessage());
            return false;
        }
    }

    private Path getFilePathToObject(String hash) {
        String identifier = hash.substring(0, 2);
        String fileName = hash.substring(2);

        return Paths.get(gitterParentPath.toString(), GITTER, OBJECTS, identifier, fileName);
    }
}

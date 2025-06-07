package tool.gitter.utils;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileUtil {

    public static String getPresentWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public static void getAllFilesRecursively(File directory, List<File> fileList) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    if (basicFileAttributes.isRegularFile()) {
                        fileList.add(file);
                    } else if (basicFileAttributes.isDirectory()) {
                        getAllFilesRecursively(file, fileList);
                    }
                } catch (Exception e) {
                    System.out.printf("Error : \nFile : %s\n%s : %s\n", file.getAbsolutePath(), e.getClass().getName(), e.getMessage());
                }
            }
        }
    }

    public static Path createFile(Path currPath, String fileName) throws Exception {
        Path pathToFile = currPath.resolve(fileName);
        return Files.createFile(pathToFile);
    }

    public static Path createFolder(Path currPath, String directoryName) throws Exception {
        Path pathToDirectory = Paths.get(currPath.toString(), directoryName);
        return Files.createDirectories(pathToDirectory);
    }

    public static void saveFile(Path target, File file) throws Exception{
        Files.createDirectories(target.getParent());

        try (
                InputStream in = new FileInputStream(file);
                OutputStream out = new FileOutputStream(target.toFile())
                ) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public static boolean fileExists(Path filePath) throws Exception {
        return Files.exists(filePath);
    }
    public static <T extends Serializable> void saveObject(T object, Path path) throws Exception {
        Files.createDirectories(path.getParent());
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path.toString()))) {
            out.writeObject(object);
        }
    }

    public static Object deserialize(Path path) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path.toString()))) {
            return in.readObject();
        }
    }

    public static String returnFileContents(Path path) throws Exception {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

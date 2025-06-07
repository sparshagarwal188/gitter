package tool.gitter.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Directory implements Serializable {
    private String path;
    Map<String, String> directoryPathToHashStringMap;
    Map<String, String> filePathToHashStringMap;

    public Directory() {
        directoryPathToHashStringMap = new HashMap<>();
        filePathToHashStringMap = new HashMap<>();
    }

    public static Directory emptyDirectory() {
        return new Directory();
    }
}

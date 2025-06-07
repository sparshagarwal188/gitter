package tool.gitter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.*;

@Getter
@Setter
public class Index implements Serializable {

    private static final long serialVersionUID = 1L;
    private Set<IndexedFile> index;

    public Index() {
        index = new HashSet<>();
    }

    public void add(IndexedFile indexedFile) {
        remove(indexedFile);

        this.index.add(indexedFile);
    }

    public void remove(IndexedFile indexedFile) {
        index.remove(indexedFile);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexedFile implements Serializable {
        private static final long serialVersionUID = 1L;

        private String filename;
        private String filepath;
        private String objectHash;

        @Override
        public boolean equals(Object ob) {
            return ob instanceof IndexedFile && this.getFilepath().equals(((IndexedFile) ob).filepath);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}

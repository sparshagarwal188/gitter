package tool.gitter.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
public class Commit implements Serializable {

    private static final long serialVersionUID = 1L;

    private String message;
    private String author;
    private String parentHash;
    private String contentTreeHash;
    private Date date;

    public String toString() {
        return String.join(";", parentHash == null ? "null" : parentHash, author, message);
    }

    public String toDisplayString(String commitHash) {
        return "\n" +
                String.format("commit : %s\n", commitHash) +
                String.format("author : %s\n", this.author) +
                String.format("date : %s\n", this.date.toString()) +
                String.format("%s\n\n", this.message);
    }
}

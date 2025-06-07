package tool.gitter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class HEAD implements Serializable {

    private static final long serialVersionUID = 1L;

    private String latestCommitHash;
}

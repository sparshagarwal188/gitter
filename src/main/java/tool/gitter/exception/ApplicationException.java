package tool.gitter.exception;

public class ApplicationException extends RuntimeException {

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException() {return;}
}

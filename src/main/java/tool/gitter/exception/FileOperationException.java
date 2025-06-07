package tool.gitter.exception;

import tool.gitter.dao.FileObjectPersister;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileOperationException extends Exception {

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileOperationException(Throwable cause) {
        super(cause.getClass().getSimpleName() + " : " + cause.getMessage(), cause);
    }
}
package edu.hm.cs.iua.exceptions.storage;

import java.io.IOException;

public class StorageOperationException extends StorageException {

    public StorageOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

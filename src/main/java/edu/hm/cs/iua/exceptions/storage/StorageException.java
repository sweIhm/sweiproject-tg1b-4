package edu.hm.cs.iua.exceptions.storage;

import edu.hm.cs.iua.exceptions.IUAException;

public abstract class StorageException extends IUAException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }

}

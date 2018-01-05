package edu.hm.cs.iua.exceptions.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class StorageInitializeException extends StorageException {

    public StorageInitializeException(String message) {
        super(message);
    }

    public StorageInitializeException(String message, Throwable cause) {
        super(message, cause);
    }


}

package edu.hm.cs.iua.exceptions.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class StorageAccessException extends StorageException {

    public StorageAccessException(String message) {
        super(message);
    }

}

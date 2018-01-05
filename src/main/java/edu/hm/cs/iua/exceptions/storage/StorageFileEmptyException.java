package edu.hm.cs.iua.exceptions.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class StorageFileEmptyException extends StorageException {

    public StorageFileEmptyException(String message) {
        super(message);
    }

}

package edu.hm.cs.iua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends LoginException {

    public InvalidPasswordException() {
        super();
    }

    public InvalidPasswordException(String message) {
        super(message);
    }
}
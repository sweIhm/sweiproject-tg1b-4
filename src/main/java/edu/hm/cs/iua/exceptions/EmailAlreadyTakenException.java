package edu.hm.cs.iua.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyTakenException extends RegistrationException {

    public EmailAlreadyTakenException() {
        super();
    }

    public EmailAlreadyTakenException(String message) {
        super(message);
    }
}
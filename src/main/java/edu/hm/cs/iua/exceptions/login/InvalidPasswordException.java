package edu.hm.cs.iua.exceptions.login;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidPasswordException extends LoginException {

    public InvalidPasswordException() {
        super();
    }

}
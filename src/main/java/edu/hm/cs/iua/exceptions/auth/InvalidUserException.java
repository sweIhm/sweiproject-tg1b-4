package edu.hm.cs.iua.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class InvalidUserException extends AuthException {

    public InvalidUserException() {
        super();
    }

}

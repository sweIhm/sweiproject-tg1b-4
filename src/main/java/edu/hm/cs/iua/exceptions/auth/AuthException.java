package edu.hm.cs.iua.exceptions.auth;

import edu.hm.cs.iua.exceptions.IUAExceptions;

public abstract class AuthException extends IUAExceptions {

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
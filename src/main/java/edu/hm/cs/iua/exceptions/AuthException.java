package edu.hm.cs.iua.exceptions;

public abstract class AuthException extends IUAExceptions {

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }
}
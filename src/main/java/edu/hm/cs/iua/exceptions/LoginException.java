package edu.hm.cs.iua.exceptions;

public abstract class LoginException extends IUAExceptions {

    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }
}
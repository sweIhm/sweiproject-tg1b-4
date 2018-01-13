package edu.hm.cs.iua.exceptions.auth;

import edu.hm.cs.iua.exceptions.IUAException;

public abstract class AuthException extends IUAException {

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }

}
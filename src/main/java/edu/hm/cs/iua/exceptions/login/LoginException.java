package edu.hm.cs.iua.exceptions.login;

import edu.hm.cs.iua.exceptions.IUAException;

public abstract class LoginException extends IUAException {

    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }
}
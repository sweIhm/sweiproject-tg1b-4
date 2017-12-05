package edu.hm.cs.iua.exceptions.login;

import edu.hm.cs.iua.exceptions.IUAExceptions;

public abstract class LoginException extends IUAExceptions {

    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }
}
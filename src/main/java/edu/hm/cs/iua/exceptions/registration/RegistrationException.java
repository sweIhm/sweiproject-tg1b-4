package edu.hm.cs.iua.exceptions.registration;

import edu.hm.cs.iua.exceptions.IUAExceptions;

public abstract class RegistrationException extends IUAExceptions {

    public RegistrationException() {
        super();
    }

    public RegistrationException(String message) {
        super(message);
    }
}
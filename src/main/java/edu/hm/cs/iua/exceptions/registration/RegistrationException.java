package edu.hm.cs.iua.exceptions.registration;

import edu.hm.cs.iua.exceptions.IUAException;

public abstract class RegistrationException extends IUAException {

    public RegistrationException() {
        super();
    }

    public RegistrationException(String message) {
        super(message);
    }
}
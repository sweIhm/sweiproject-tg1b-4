package edu.hm.cs.iua.exceptions;

public class WrongPasswordException extends IUAExceptions {
    public WrongPasswordException() {
        super("Password is incorrect.");
    }
}

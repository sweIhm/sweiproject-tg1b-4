package base.exceptions;

public class WrongPasswordException extends IUAExceptions {
    public WrongPasswordException() {
        super("Password is incorrect.");
    }
}

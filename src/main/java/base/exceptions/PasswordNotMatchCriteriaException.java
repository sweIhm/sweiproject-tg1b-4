package base.exceptions;

public class PasswordNotMatchCriteriaException extends IUAExceptions {
    public PasswordNotMatchCriteriaException() {
        super("Password does not match the given Criteria.");
    }
}

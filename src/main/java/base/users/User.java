package base.users;
import base.exceptions.PasswordNotMatchCriteriaException;

public class User {
    private String userName;
    private String userEmail;
    private String userPassword;
    private String codeSendToUser;
    private boolean confirmedAcc;
    private boolean codeSend;
    private int registrationDate;

    public User() {
        userName = "anomynous";
        userEmail = "unknown";
        userPassword = null;
        confirmedAcc = false;
        codeSend = false;
        registrationDate = 0;
    }
    private User(User user) {}
    private User(String name, String email, String password) {
        userName = name;
        userEmail = email;
        userPassword = encryptPassword(password);
        confirmedAcc = false;
        codeSend = false;
        // registrationDate = System.getDate();
    }

    public User registration(String name, String email, String password) throws PasswordNotMatchCriteriaException {
        if (!testPassword(password))    // Could be tested in JavaScript
            throw new PasswordNotMatchCriteriaException();
        return new User(name, email, encryptPassword(password));
    }
    public boolean login(String email, String password) {
        return false;
    }
    public void sendConfirmationCode() {
        // Send Code to Email
        // If already send, show alert.
        codeSend = true;
        codeSendToUser = "";
    }
    public void confirmActivationCode(String code) {
        // If code is same as send code.
        confirmedAcc = true;
    }

    private String encryptPassword(String password) {
        return "";
    }
    private String decryptPassword(String encryptedPassword) {
        return "";
    }
    private boolean comparePassword(String inputPassword) {
        return false;
    }
    private boolean testPassword(String password) {
        return false;
    }
    private String generateCode() {
        return "";
    }
}

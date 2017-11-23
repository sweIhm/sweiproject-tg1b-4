package base.users;

public class User {
    private String userName;
    private String userEmail;
    private String userPassword;
    private int registrationDate;

    public User() {}
    private User(User user) {}
    private User(String name, String email, String password) {}

    public boolean registration(String name, String email, String password) {return false;}
    public boolean login(String email, String password) {return false;}

    private void encryptPassword(String password) {}
    private void decryptPassword(String encryptedPassword) {}
    private boolean comparePassword(String inputPassword) {return false;}
}

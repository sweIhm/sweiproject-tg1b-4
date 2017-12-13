package edu.hm.cs.iua.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class IUAUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
    private String name;
    private String password;
    private boolean isValidated;
    private String confirmationCode;

    public IUAUser() {}

    public IUAUser(String name, String email, String password, String confirmationCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isValidated = false;
        this.confirmationCode = confirmationCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public UserProfile getProfile() {
        return new UserProfile(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IUAUser iuaUser = (IUAUser) o;

        return isValidated == iuaUser.isValidated
                && (id != null ? id.equals(iuaUser.id) : iuaUser.id == null)
                && (email != null ? email.equals(iuaUser.email) : iuaUser.email == null)
                && (name != null ? name.equals(iuaUser.name) : iuaUser.name == null)
                && (password != null ? password.equals(iuaUser.password) : iuaUser.password == null)
                && (confirmationCode != null ? confirmationCode.equals(iuaUser.confirmationCode) : iuaUser.confirmationCode == null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (isValidated ? 1 : 0);
        result = 31 * result + (confirmationCode != null ? confirmationCode.hashCode() : 0);
        return result;
    }
}
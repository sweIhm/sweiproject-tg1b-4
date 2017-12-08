package edu.hm.cs.iua.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Nutzer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String email;
    private String password;
    private boolean isValidated;
    private String confirmationCode;

    public Nutzer() {}

    public Nutzer(String name, String email, String password, String confirmationCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isValidated = false;
        this.confirmationCode = confirmationCode;
    }

    public Long getId() {
        return id;
    }

    public Nutzer setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Nutzer setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getName() {
        return name;
    }

    public Nutzer setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Nutzer setPassword(String password) {
        this.password = password;
        return this;
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

}
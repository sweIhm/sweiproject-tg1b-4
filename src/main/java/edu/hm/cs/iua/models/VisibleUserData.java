package edu.hm.cs.iua.models;

public class VisibleUserData {

    public static VisibleUserData getUserData(User user) {
        return new VisibleUserData(user.getName());
    }

    private final String name;

    private VisibleUserData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

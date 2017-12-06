package edu.hm.cs.iua.models;

public class VisibleUserData {

    public static VisibleUserData getUserData(User user) {
        return new VisibleUserData(user.getId(),user.getName());
    }

    private final Long id;
    private final String name;

    private VisibleUserData(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

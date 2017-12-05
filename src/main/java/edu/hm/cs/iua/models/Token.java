package edu.hm.cs.iua.models;

import edu.hm.cs.iua.utils.TokenGenerator;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {

    private static final TokenGenerator generator = new TokenGenerator();

    @Id
    private Long id;
    private String key;

    public Token() {}

    public Token(Long id) {
        this.id = id;
        this.key = generator.nextToken();
    }

    public Long getId() {
        return id;
    }

    public Token setId(Long id) {
        this.id = id;
        return this;
    }

    public String getToken() {
        return key;
    }

    public Token setToken(String token) {
        this.key = token;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token1 = (Token) o;

        return key.equals(token1.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

}
package edu.hm.cs.iua.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class TokenGenerator {

    private static final int STRENGTH = 256;
    private static final int BASE = 64;

    private SecureRandom random = new SecureRandom();

    public synchronized String nextToken() {
        return new BigInteger(STRENGTH, random).toString(BASE);
    }

}
package edu.hm.cs.iua;

import org.junit.Test;

public class ApplicationTest {

    @Test
    public void successfulStartTest() {
        Application.main("--spring.main.web-environment=false", "--spring.autoconfigure.exclude=test");
    }

}

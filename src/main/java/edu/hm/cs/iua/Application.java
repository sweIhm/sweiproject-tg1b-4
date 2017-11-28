package edu.hm.cs.iua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("starting IUA app....");
        SpringApplication.run(Application.class, args);
    }

}

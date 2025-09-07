package love.disaster.j2j.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * J2J Web Application main class
 * Provides a web interface for validating and testing J2J JSON transformations
 */
@SpringBootApplication
public class J2JWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(J2JWebApplication.class, args);
    }
}
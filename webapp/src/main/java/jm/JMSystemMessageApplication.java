package jm;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JMSystemMessageApplication {
    private static final Logger logger = LoggerFactory.getLogger(JMSystemMessageApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(JMSystemMessageApplication.class);
    }
}

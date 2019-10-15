package jm.config;


import jm.WebSecurityConfig;
import jm.config.inititalizer.TestDataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@Import(WebSecurityConfig.class)
@EnableJpaRepositories(value = {"jm.dao", "jm.api.dao"})
@EntityScan("jm.model")
@ComponentScan("jm")
public class JMSystemMessageApplication {
    private static final Logger logger = LoggerFactory.getLogger(JMSystemMessageApplication.class);

    @Bean(initMethod = "init")
    @PostConstruct
    public TestDataInitializer initTestData() {
        return new TestDataInitializer();
    }

    public static void main(String[] args) {
        SpringApplication.run(JMSystemMessageApplication.class);
    }
}

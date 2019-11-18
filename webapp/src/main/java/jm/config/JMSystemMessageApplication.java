package jm.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import jm.config.inititalizer.TestDataSecurityInitializer;
import jm.config.inititalizer.TestDataInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableJpaRepositories(value = {"jm.dao", "jm.api.dao"})
@EntityScan("jm.model")
@ComponentScan("jm")
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class JMSystemMessageApplication {
    private static final Logger logger = LoggerFactory.getLogger(JMSystemMessageApplication.class);

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        mapper.registerModule(javaTimeModule);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return new MappingJackson2HttpMessageConverter(mapper);
    }

    @Bean(initMethod = "init")
    @PostConstruct
    public TestDataInitializer initTestData() {
        return new TestDataInitializer();
    }

//    @Bean(initMethod = "init")
//    @PostConstruct
//    public TestDataSecurityInitializer initTestSecurityData() {
//        return new TestDataSecurityInitializer();
//    }

    public static void main(String[] args) {
        SpringApplication.run(JMSystemMessageApplication.class);
    }
}
